package com.squill.og.crawler.internal;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.squill.og.crawler.content.handlers.ExpressionContext;
import com.squill.og.crawler.content.handlers.ExpressionContext.EvalContext;

/**
 * 
 * @author Saurav
 *
 */
public class LinkFilterConditionEvaluator {

	private ExpressionParser parser;

	public LinkFilterConditionEvaluator() {
		parser = new SpelExpressionParser();
	}

	public boolean evalExp(final String expression) {
		if (expression == null || expression.trim().isEmpty())
			return false;
		return eval(expression, ExpressionContext.get());
	}

	private boolean eval(final String expression, EvalContext evalContext) {
		if (evalContext == null || evalContext.get$_link() == null) {
			return false;
		}
		String $_link = evalContext.get$_link();
		String expressionNew = expression.replaceAll("$_link", $_link);
		return eval0(expressionNew);
	}

	private boolean eval0(String expression) {
		Expression exp = parser.parseExpression(expression);
		boolean result = (Boolean) exp.getValue();
		return result;
	}
}
