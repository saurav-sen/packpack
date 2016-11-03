package com.squill.og.crawler.content.handlers;

/**
 * 
 * @author Saurav
 *
 */
public class ExpressionContext {

	private static ThreadLocal<ExpressionContext.EvalContext> context = new ThreadLocal<ExpressionContext.EvalContext>();

	public static EvalContext get() {
		return context.get();
	}

	public static void set(EvalContext ctx) {
		context.set(ctx);
	}

	public static final class EvalContext {

		private String $_link;
		
		public EvalContext(String $_link) {
			set$_link($_link);
		}

		public String get$_link() {
			return $_link;
		}

		public void set$_link(String $_link) {
			this.$_link = $_link;
		}
	}
}