package com.diligence.rules;

public record CreateRuleRequest(
    String name,
    String description,
    RuleType ruleType,
    RuleSeverity severity
) {}
