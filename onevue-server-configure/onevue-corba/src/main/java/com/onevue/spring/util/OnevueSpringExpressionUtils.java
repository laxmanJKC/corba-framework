package com.onevue.spring.util;

import static com.onevue.spring.constants.CorbaConstants.EXPRESSION_CORBA_ORB_OBJ;
import static com.onevue.spring.constants.CorbaConstants.EXPRESSION_CORBA_OBJECT_TIE;

import org.omg.CORBA.ORB;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public abstract class OnevueSpringExpressionUtils {
	
	public static EvaluationContext evaluationContext(Object rootObj) {
		if (rootObj == null) {
			return null;
		}
		return new StandardEvaluationContext(rootObj);
	}
	
	public static SpelParserConfiguration spelParserConfiguration(boolean autoGrowNullReferences, boolean autoGrowCollections) {
		return new SpelParserConfiguration(autoGrowNullReferences, autoGrowCollections);
	}
	
	public static SpelParserConfiguration defaultSpelParserConfiguration() {
		return spelParserConfiguration(true, true);
	}
	
	public static Object corbaObjectByTie(Object corbaTieObj, ORB orb) {
		if (orb == null || corbaTieObj == null) {
			return null;
		}
		EvaluationContext evaluationContext = evaluationContext(corbaTieObj);
		evaluationContext.setVariable(EXPRESSION_CORBA_ORB_OBJ, orb);
		SpelParserConfiguration spelParserConfiguration = defaultSpelParserConfiguration();
	    ExpressionParser parser = new SpelExpressionParser(spelParserConfiguration);
	    Expression expression = parser.parseExpression(EXPRESSION_CORBA_OBJECT_TIE);
	    return expression.getValue(evaluationContext);
	}
}
