package parse;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List; 
import ast.Action;
import ast.ActionImpl;
import ast.BinaryCondition;
import ast.Condition;
import ast.Expr;
import ast.FactorExpr;
import ast.MemExpr;
import ast.NumExpr;
import ast.Program;
import ast.ProgramImpl;
import ast.RelationCondition;
import ast.Rule;
import ast.SensorExpr;
import ast.TermExpr;
import ast.TermExpr.Operator;
import ast.UnaryExpr;
import ast.Update;
import ast.UpdateImpl;
import exceptions.SyntaxError;

class ParserImpl implements Parser {
	
	@Override
	public Program parse(Reader r) {
		Tokenizer t = new Tokenizer(r); 
		try {
			return parseProgram(t);
		} catch (SyntaxError e) {
			e.printStackTrace();
			System.out.println("Invalid Syntax in the Critter Program");
		} 
		return null;
	}

	/**
	 * Parses a program from the stream of tokens provided by the Tokenizer,
	 * consuming tokens representing the program. All following methods with a
	 * name "parseX" have the same spec except that they parse syntactic form X.
	 * 
	 * @return the created AST
	 * @throws SyntaxError
	 *            if there the input tokens have invalid syntax
	 */
	public static ProgramImpl parseProgram(Tokenizer t) throws SyntaxError {
		ProgramImpl p = new ProgramImpl();
		while(t.hasNext()) {
			p.add(parseRule(t));
		}
		return p;
	}

	public static Rule parseRule(Tokenizer t) throws SyntaxError {
		Condition c = parseCondition(t);   
		Rule r = new Rule(c);
		                                                                                                                                                                                      
		
		if (t.peek().getType().equals(TokenType.ARR)) {
			
			consume(t, TokenType.ARR);
			while (t.hasNext() && !t.peek().getType().equals(TokenType.SEMICOLON)) {
				if (t.peek().getType().equals(TokenType.MEM) || t.peek().getType().category().equals(TokenCategory.MEMSUGAR)) {	
					r.addUpdate(parseUpdate(t));
				}
				else if(t.peek().getType().category().equals(TokenCategory.ACTION)) {
					if (r.getAction() != null) {
						throw new SyntaxError();
					}
					r.setAction(parseAction(t)); 
				}
				else {
					throw new SyntaxError(); 
				}                                                                                                                                                 
			}
			
			consume(t, TokenType.SEMICOLON);
		}
		else {
			throw new SyntaxError(); 
		}
		return r;
	}
		

	public static Condition parseCondition(Tokenizer t) throws SyntaxError {
		Condition c = null; 

		if (t.peek().getType().equals(TokenType.LBRACE)) {
			
			consume(t, TokenType.LBRACE);
			c = parseCondition(t); 
			
			consume(t, TokenType.RBRACE); 
			if (t.peek().getType().equals(TokenType.AND) || t.peek().getType().equals(TokenType.OR)) {
				
				TokenType type = t.peek().getType();
				consume(t, t.peek().getType());
				if (type.equals(TokenType.AND)) {
					c = new BinaryCondition(c, BinaryCondition.Operator.AND, parseCondition(t)); 
				}
				else {
					c = new BinaryCondition(c, BinaryCondition.Operator.OR, parseCondition(t));
				}
			}
		}
		else {
			Expr x = parseExpression(t); 
			TokenType rel = t.peek().getType(); 
			RelationCondition.Operator pol; 
			switch(rel) {
			case LT:
				pol = RelationCondition.Operator.LT; 
				break;
			case GT:
				pol = RelationCondition.Operator.GT;
				break;
			case GE:
				pol = RelationCondition.Operator.GE;
				break;
			case LE:
				pol = RelationCondition.Operator.LE;
				break;
			case EQ:
				pol = RelationCondition.Operator.EQ;
				break;
			case NE:
				pol = RelationCondition.Operator.NE;
				break;
			default:
				throw new SyntaxError();
			}
			
			consume(t, rel);
			Expr y = parseExpression(t); 

			c = new RelationCondition(x, pol, y); 

			while (t.peek().getType().equals(TokenType.AND) ||
					t.peek().getType().equals(TokenType.OR)) {

				TokenType type = t.peek().getType(); 
				
				consume(t, type);
				if (type.equals(TokenType.AND)) {
					c = new BinaryCondition(c, BinaryCondition.Operator.AND, parseCondition(t)); 
				}
				else {
					c = new BinaryCondition(c, BinaryCondition.Operator.OR, parseCondition(t));
				}

			}
		}
		return c; 
	}

	public static Update parseUpdate(Tokenizer t) throws SyntaxError{
		Update u = null;
		
		Expr e = parseExpression(t);
		
		consume(t, TokenType.ASSIGN);
		
		u = new UpdateImpl(e, parseExpression(t));
		return u; 
	}
	
	public static Action parseAction(Tokenizer t) throws SyntaxError{
		ActionImpl a = null; 
		
		if (!t.peek().isAction()) {
			
			throw new SyntaxError();
		}
		else {
			TokenType type = t.peek().getType();
			
			consume(t, type);
			
			switch(type) {
			case WAIT:
				a = new ActionImpl(ActionImpl.Actions.WAIT);
				break;
			case FORWARD:
				a = new ActionImpl(ActionImpl.Actions.FORWARD);
				break;
			case SERVE:
				consume(t, TokenType.LBRACKET);
				a = new ActionImpl(ActionImpl.Actions.SERVE, parseExpression(t)); 
				consume(t, TokenType.RBRACKET); 
				break;
			case BACKWARD:
				a = new ActionImpl(ActionImpl.Actions.BACKWARD);
				break;
			case LEFT:
				a = new ActionImpl(ActionImpl.Actions.LEFT);
				break;
			case TAG:
				consume(t, TokenType.LBRACKET);
				a = new ActionImpl(ActionImpl.Actions.TAG, parseExpression(t));
				consume(t, TokenType.RBRACKET); 
				break;
			case RIGHT:
				a = new ActionImpl(ActionImpl.Actions.RIGHT);
				break;
			case EAT:
				a = new ActionImpl(ActionImpl.Actions.EAT); 
				break;
			case ATTACK:
				a = new ActionImpl(ActionImpl.Actions.ATTACK); 
				break;
			case GROW:
				a = new ActionImpl(ActionImpl.Actions.GROW); 
				break;
			case BUD:
				a = new ActionImpl(ActionImpl.Actions.BUD);
				break;
			case MATE:
				a = new ActionImpl(ActionImpl.Actions.MATE);
				break;
			}
		}
		return a; 
	}

	public static Expr parseExpression(Tokenizer t) throws SyntaxError {
		Expr e = parseTerm(t); 
		while (t.peek().getType().category().equals(TokenCategory.ADDOP)) {
			TokenType type = t.peek().getType();
			
			consume(t, type);
			if (type.equals(TokenType.PLUS)) {
				e = new TermExpr(e, Operator.PLUS, parseTerm(t)); 
			}
			else {
				e = new TermExpr(e, Operator.MINUS, parseTerm(t)); 
			}
		}
		return e; 
	}

	public static Expr parseTerm(Tokenizer t) throws SyntaxError {
		Expr e = parseSimpleFactor(t);
		
		while (t.peek().isMulOp()) {
			TokenType type = t.peek().getType();
			consume(t, type); 
			switch(type) {
			case DIV:
				e = new FactorExpr(e, FactorExpr.Operator.DIV, parseSimpleFactor(t)); 
				break;
			case MUL:
				e = new FactorExpr(e, FactorExpr.Operator.MUL, parseSimpleFactor(t));
				break;
			case MOD:
				
				e = new FactorExpr(e, FactorExpr.Operator.MOD, parseSimpleFactor(t)); 
				
				break;
			default:
				throw new SyntaxError(); 
			}
			
		}
		return e; 
		
	}
	public static Expr parseSimpleFactor(Tokenizer t) throws SyntaxError {
		Expr e = null; 
		if (t.peek().isNum()) {
			int value = t.peek().toNumToken().getValue();
			consume(t, TokenType.NUM); 
			e = new NumExpr(value); 
		}
		else if (t.peek().getType().equals(TokenType.MINUS)){
			
			consume(t, TokenType.MINUS);
			if (t.peek().isNum()) {
				
				e = new NumExpr(0 - t.next().toNumToken().getValue());
			}
			else if (t.peek().getType().equals(TokenType.LPAREN)) {
				consume(t, TokenType.LPAREN);
				e = new UnaryExpr(parseExpression(t)); 
				consume(t, TokenType.RPAREN);
			}
			else {
				throw new SyntaxError(); 
			}
		}
		else if (t.peek().getType().equals(TokenType.MEM)){
			
			consume(t, TokenType.MEM);
			
			consume(t, TokenType.LBRACKET);
			e = new MemExpr(parseExpression(t));
			consume(t, TokenType.RBRACKET);
			return e;
		}
		else if (t.peek().getType().category().equals(TokenCategory.MEMSUGAR)) {
			TokenType type = t.peek().getType(); 
			
			consume(t, type); 
		
			switch(type) {
			case ABV_MEMSIZE:
				e = new MemExpr(MemExpr.MemSugar.MEMSIZE);
				break;
			case ABV_DEFENSE:
				e = new MemExpr(MemExpr.MemSugar.DEFENSE);
				break;
			case ABV_OFFENSE:
				e = new MemExpr(MemExpr.MemSugar.OFFENSE);
				break;
			case ABV_SIZE:
				e = new MemExpr(MemExpr.MemSugar.SIZE);
				break;
			case ABV_ENERGY:
				e = new MemExpr(MemExpr.MemSugar.ENERGY);
				break;
			case ABV_PASS:
				e = new MemExpr(MemExpr.MemSugar.PASS);
				break;
			case ABV_TAG:
				e = new MemExpr(MemExpr.MemSugar.TAG);
				break;
			case ABV_POSTURE:
				e = new MemExpr(MemExpr.MemSugar.POSTURE);
				break; 
			default:
				throw new SyntaxError();
			}
		}
		else if (t.peek().getType().equals(TokenType.LPAREN)) {
			consume(t, TokenType.LPAREN);
			e = parseExpression(t); 
			consume(t, TokenType.RPAREN); 
		}
		else if (t.peek().getType().category().equals(TokenCategory.SENSOR)) {
			TokenType type = t.peek().getType();
			
			consume(t, type);
			if (t.peek().getType().equals(TokenType.LBRACKET) && !type.equals(TokenType.SMELL)){
				consume(t, TokenType.LBRACKET); 
			}
			switch(type) {
			case NEARBY:
				e = new SensorExpr(parseExpression(t), SensorExpr.SensorType.NEARBY);
				break;
			case AHEAD:
				e = new SensorExpr(parseExpression(t), SensorExpr.SensorType.AHEAD);
				break;
			case SMELL:
				e = new SensorExpr(SensorExpr.SensorType.SMELL);
				break;
			case RANDOM:
				e = new SensorExpr(parseExpression(t), SensorExpr.SensorType.RANDOM);
				break;
			default:
				throw new SyntaxError(); 
			}
			if (t.peek().getType().equals(TokenType.RBRACKET)) {
				consume(t, TokenType.RBRACKET); 
			}
		}
		else {
			throw new SyntaxError(); 
		}
		return e;
	}


	/**
	 * Consumes a token of the expected type.
	 * 
	 * @throws SyntaxError
	 *            if the wrong kind of token is encountered.
	 */
	public static void consume(Tokenizer t, TokenType tt) throws SyntaxError {
		if (t.peek().getType().equals(tt)) {
			t.next(); 
		}
		else {
			throw new SyntaxError(); 
		}
	}
}
