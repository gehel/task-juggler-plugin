#!/usr/bin/env ruby
# Extracts TaskJuggler keyword metadata from the locally installed TaskJuggler
# (the same data tj3man uses) into a JSON file consumed by the plugin.
#
# Usage: ruby tools/extract-keywords.rb > src/main/resources/tj3/keywords.json

require 'json'
require 'taskjuggler/Tj3Config'
require 'taskjuggler/SyntaxReference'
require 'taskjuggler/RichText'

SyntaxReference = TaskJuggler::SyntaxReference
RichText = TaskJuggler::RichText

AppConfig.appName = 'tj3man'
ref = SyntaxReference.new
rules = ref.instance_variable_get(:@parser).rules

def rich_html(text)
  return nil if text.nil? || text.empty?
  rt = RichText.new(text, [])
  rti = rt.generateIntermediateFormat
  rti.linkTarget = '_blank' if rti.respond_to?(:linkTarget=)
  # Make links to other keywords point to the online manual.
  rti.to_html.to_s.gsub(/href="([\w.]+)\.html"/,
                        'href="https://taskjuggler.org/tj3/manual/\\1.html"')
rescue StandardError
  text
end

def rich_text(text)
  return nil if text.nil? || text.empty?
  RichText.new(text, []).generateIntermediateFormat.to_s.strip
rescue StandardError
  text
end

# Literal values accepted by a rule whose patterns each consist of exactly one
# literal token (e.g. scheduling asap|alap). Follows single-reference patterns.
def enum_values(rules, rule_name, seen = {})
  return [] if seen[rule_name]
  seen[rule_name] = true
  rule = rules[rule_name]
  return [] unless rule
  values = []
  rule.patterns.each do |p|
    toks = p.tokens
    if toks.length == 1 && toks[0][0] == :literal
      values << toks[0][1]
    elsif toks.length == 1 && toks[0][0] == :reference
      values += enum_values(rules, toks[0][1], seen)
    end
  end
  values
end

keywords = {}
ref.keywords.each do |id, kwd|
  pattern = kwd.pattern
  args = kwd.instance_variable_get(:@args) || []
  first = pattern.tokens.index { |t| t[0] == :literal }
  values = []
  if first
    pattern.tokens[(first + 1)..-1].each do |type, name|
      next unless type == :reference
      v = enum_values(rules, name)
      values += v if v.all? { |x| x =~ /\A[a-zA-Z][\w]*\z/ }
    end
  end
  syntax = kwd.instance_variable_get(:@syntax)
  # Plain words in the syntax (outside of <placeholders>) after the keyword
  # itself are literal values, e.g. 'formats (csv | html)' or 'off'.
  syntax.gsub(/<[^>]*>/, ' ').split(/[^a-zA-Z_]+/).drop(1).each do |w|
    values << w unless w.empty? || w == 'attributes'
  end
  keywords[id] = {
    'name' => kwd.names.first || id.split('.').first,
    'title' => kwd.title,
    'supportLevel' => pattern.supportLevel.to_s,
    'purpose' => rich_text(pattern.doc),
    'purposeHtml' => rich_html(pattern.doc),
    'syntax' => syntax == '[{ <attributes> }]' ? nil : syntax,
    'args' => args.map do |a|
      { 'name' => a.name, 'type' => a.typeSpec, 'doc' => rich_text(a.text) }
    end,
    'contexts' => kwd.contexts.map(&:keyword).sort,
    'globalScope' => !!kwd.globalScope?,
    'attributes' => kwd.optionalAttributes.map(&:keyword).sort,
    'property' => !!kwd.isProperty?,
    'listAttribute' => !!kwd.listAttribute?,
    'scenarioSpecific' => !!kwd.scenarioSpecific,
    'inheritedFromProject' => !!kwd.inheritedFromProject,
    'inheritedFromParent' => !!kwd.inheritedFromParent,
    'seeAlso' => kwd.instance_variable_get(:@seeAlso).map(&:keyword),
    'values' => values.uniq.sort
  }
end

# Every alphabetic literal appearing anywhere in the grammar.
literals = []
rules.each_value do |rule|
  rule.patterns.each do |p|
    p.tokens.each do |type, name|
      literals << name if type == :literal && name =~ /\A[a-zA-Z]\w*\z/
    end
  end
end
keyword_names = keywords.values.map { |k| k['name'] }.uniq.sort

out = {
  'tjVersion' => AppConfig.version,
  'keywordNames' => keyword_names,
  'valueLiterals' => (literals.uniq - keyword_names).sort,
  'keywords' => keywords.keys.sort.map { |k| [k, keywords[k]] }.to_h
}
puts JSON.pretty_generate(out)
