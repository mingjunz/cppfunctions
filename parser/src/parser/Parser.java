package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	private String file;
	private char[] content;

	// do more check to debug
	static private boolean checkMode = false;
	static private boolean buildCharToLine = true;
	private ArrayList<Integer> linesMap = new ArrayList<Integer>();

	int currentPos = 0;

	static private char[] blankChars = { ' ', '\t', '\r', '\n' };

	private void errorMsg(String messege, boolean printTrace) {
		System.err.println(messege);
		if (printTrace) {
			// TODO
		}
	}

	private void infoMsg(String messege, boolean printTrace) {
		System.out.println(messege);
		if (printTrace) {
			// TODO
		}
	}

	private List<FunctionDefinition> fucntions = new ArrayList<FunctionDefinition>();

	public List<FunctionDefinition> getFucntions() {
		return fucntions;
	}

	public void setFucntions(List<FunctionDefinition> fucntions) {
		this.fucntions = fucntions;
	}

	private void buildLinePosition() {
		// start from 1 not zero
		linesMap.add(-1);

		int count = 0;
		int end = content.length;
		while (count < end) {
			if (content[count] == '\n') {
				linesMap.add(count);
			}
			count++;
		}

		// if we didnt end with empty line, then will need the last extra step
		if (content[end - 1] != '\n') {
			linesMap.add(end - 1);
		}

	}

	/**
	 * Give the char position, return the line number
	 * 
	 * @param position
	 * @return
	 */
	private int getLineNumber(int position) {
		int start = 1;
		int end = linesMap.size();

		boolean found = false;
		while (!found) {
			int middle = (start + end) / 2;

			int pos = linesMap.get(middle);
			if (position == pos) {
				return middle;
			} else if (position > pos) {
				int pos1 = linesMap.get(middle + 1);
				if (position <= pos1) {
					return middle + 1;
				} else {
					start = middle + 1;
				}
			} else {
				int pos1 = linesMap.get(middle - 1);

				if (position > pos1) {
					return middle;
				} else {
					end = middle - 1;
				}
			}

		}
		return -1;
	}

	private String getLine(int lineNumber) {
		int start = lineNumber == 1 ? 0 : (linesMap.get(lineNumber - 1) + 1);// plus
																				// 1
																				// means
																				// skip
																				// the
																				// ending
																				// '\n'
		int end = linesMap.get(lineNumber) - 1;// minus 1 means move back over
												// the ending '\n'

		return getString(content, start, end);

	}

	/**
	 * give the nth char of the file, get the line of that nth char
	 * 
	 * @param nth
	 * @return
	 */
	private String getLineFromNthChar(int nth) {
		int start = getCurrentLineStart(nth, content);
		int end = getCurrentLineEnd(nth, content.length, content);

		return getString(content, start, end);

	}

	/**
	 * Debug purpose
	 * 
	 * @param nth
	 */
	private void _printLineOfNthChar(int nth) {
		int lineNumber = getLineNumber(nth);
		String line = getLineFromNthChar(nth);

		System.out.println(nth + "th is in line " + lineNumber + " the whole text is: \n:" + line.trim());
	}

	/**
	 * just for debug purpose
	 */
	private void _testLineMap() {
		for (int i = 1; i < linesMap.size(); i++) {
			String tmp = getLine(i);
			tmp = tmp.trim();
			System.out.println(i + "th   " + tmp);
		}
	}

	public Parser(String file) {
		this.file = file;
		try {
			content = new String(Files.readAllBytes(Paths.get(file))).toCharArray();
		} catch (IOException e) {
			System.out.println("Cannot load fouce code: " + file);
			return;
		}
		if (content.length <= 0) {
			System.out.println("File is empty :" + file);
			return;
		}

		if (buildCharToLine) {
			buildLinePosition();
		}
	}

	public Parser(StringBuilder srcFile) {
		content = srcFile.toString().toCharArray();
		if (content.length <= 0) {
			System.out.println("File is empty :" + file);
			return;
		}

		if (buildCharToLine) {
			buildLinePosition();
		}

	}

	private boolean isBlankChar(char c) {
		if (c == blankChars[0] || c == blankChars[1] || c == blankChars[2] || c == blankChars[3]) {
			return true;
		} else {
			return false;
		}
	}

	public boolean parse() {
		boolean success = true;

		// load the file into memory

		FunctionDefinition nextFun = getNextFunctionDefinition(0, content.length, content);

		while (nextFun != null) {
			fucntions.add(nextFun);
			nextFun = getNextFunctionDefinition(nextFun.getEndChar() + 1, content.length, content);
		}

		return success;
	}

	int getNextNonBlankPosition(int start, int end, char[] str) {
		int current = start;
		while (isBlankChar(str[current]) && current < end) {
			current++;
		}
		if (current < end) {
			return current;
		} else {
			return -1;
		}
	}

	/**
	 *
	 * @param start
	 * @param end
	 * @param str
	 * @return -1 if it reach end
	 */
	int skipBlank(int start, int end, char[] str) {
		while (start < end && isBlankChar(str[start])) {
			start++;
		}
		if (start < end) {
			return start;
		} else {
			return -1;
		}
	}

	int skipBlankBackward(int start, char[] str) {
		while (start >= 0 && isBlankChar(str[start])) {
			start--;
		}
		if (start >= 0) {
			return start;
		} else {
			return -1;
		}
	}

	int getPreNonSpaceCharPos(int start, char[] str) {
		while (start >= 0 && isBlankChar(str[start])) {
			start--;
		}
		if (start >= 0) {
			return start;
		} else {
			return -1;
		}
	}

	/**
	 * check if the char at the start is '{', and the preivous non-commented &
	 * non-blank char is ')'
	 *
	 * @param start
	 * @param str
	 * @return
	 */
	boolean isStartOfFunctionBody(int start, char[] str) {
		if (str[start] == '{') {
			int pos = skipCommentBackward(start - 1, str);
			if (pos >= 0 && str[pos] == ')') {
				return true;
			}
		}
		return false;
	}

	/**
	 * the start is the beginning ' " ', we skip the "...
	 * ", and return the char next to ending ' " '
	 * 
	 * @param start
	 * @param end
	 * @param str
	 * @return
	 */
	int skipLiterString(int start, int end, char[] str) {
		if (str[start] != '"') {
			return start;
		} else {
			int next = start + 1;
			while (next < end) {
				// For: "abc\"abc" : quote with quote
				if (str[next] == '\\' && next + 1 < end) {
					if (str[next + 1] != '"') {
						next += 2;
					} else {
						next++;
					}
				} else if (str[next] == '"') {
					break;
				} else {
					next++;
				}
			}
			return (next < end) ? next + 1 : -1;
		}

	}

	int getNextMatchingCurlyBracket(int start, int end, char[] str) {

		// start is '{', so we start with 1
		int counter = 1;

		// Make sure we start with '{'
		if (str[start] != '{') {
			return -1;
		}
		int current = start + 1;
		boolean found = false;
		while (current < end) {

			//
			// int todebug = current;
			// int lineNumber = getLineNumber(current);
			// System.out.println(lineNumber+ "--->"+ getLine(lineNumber));

			current = skipComment(current, end, str);

			if (str[current] == '{') {
				// int lineNumber = getLineNumber(current);
				// System.out.println(lineNumber+ "--->"+ getLine(lineNumber));

				counter++;

				current++;
			} else if (str[current] == '}') {
				// int lineNumber = getLineNumber(current);
				// System.out.println(lineNumber+ "--->"+ getLine(lineNumber));
				counter--;
				if (counter == 0) {
					found = true;
					break;
				}
				current++;
			} else if (str[current] == '"') {// "..." can contains '{'
				current = skipLiterString(current, end, str);

			} else if (str[current] == '\'') {// '...' can contains '{'
				current += 4;
			} else {
				current++;
			}
		}

		if (found) {
			return current;
		} else {
			return -1;
		}

	}

	/**
	 * return the function body start of loc to end loc : {...}
	 *
	 * @param start
	 * @param end
	 * @param str
	 * @return
	 */
	int[] getFunctionBody(int start, int end, char[] str) {
		int result[] = { -1, -1 };

		if (checkMode) {
			if (!isStartOfFunctionBody(start, str)) {
				System.err.println("ERROR: getFunctionBody");
				return result;
			}
		}
		int endingCurlyBracket = getNextMatchingCurlyBracket(start, end, str);
		if (endingCurlyBracket != -1) {
			result[0] = start;
			result[1] = endingCurlyBracket;
		}

		return result;

	}

	/**
	 * Given a ')', get the position of the previous matching '('
	 *
	 * @param start
	 * @param str
	 * @return
	 */
	int getPreMatchingParentheses(int start, char[] str) {
		int result = -1;

		// start is '{', so we start with 1
		int counter = 1;

		// Make sure we start with ')'
		if (str[start] != ')') {
			return -1;
		}
		int current = start - 1;
		boolean found = false;
		while (current >= 0) {
			current = skipCommentBackward(current, str);
			if (str[current] == ')') {
				counter++;
			} else if (str[current] == '(') {
				counter--;
				if (counter == 0) {
					found = true;
					break;
				}
			}

			current--;

		}

		if (found) {
			return current;
		} else {
			return -1;
		}

	}

	/**
	 * Given a '(', get the position of the next matching ')'
	 *
	 * Testing case: ( () () (()()))
	 * 
	 * ( // )
	 * 
	 * 
	 * Multiline comments (/**)
	 * 
	 * @param start
	 * @param end
	 * @param str
	 * @return
	 */
	int getNextMatchingParentheses(int start, int end, char[] str) {
		int result = -1;

		// start is '{', so we start with 1
		int counter = 1;

		// Make sure we start with '{'
		if (str[start] != '(') {
			return -1;
		}
		int current = start + 1;
		boolean found = false;
		while (current < end) {
			current = skipComment(current, end, str);
			if (str[current] == '(') {
				counter++;
			} else if (str[current] == ')') {
				counter--;
				if (counter == 0) {
					found = true;
					break;
				}
			}
			current++;
		}

		if (found) {
			return current;
		} else {
			return -1;
		}

	}

	/**
	 * Following example is the constructor initializer, we need to deal this
	 * case Sum() : sum(0) { }
	 * 
	 * the input format is: ( ...comment... int a, int b)
	 * 
	 * @param current
	 * @param end
	 * @param str
	 * @return
	 */
	boolean isFunctionParameters(int start, int end, char[] str) {
		int next = skipComment(start + 1, end+1, str);
		
		
		if(next==end){// like void fun(){}
			return true;
		}
		int startFirstType = next;
		while (next < end) {

			// the separator can be: space comment ')'
			
			//Type name Funtion name Variable Name have the same rule
			if (!isValidCharForFunctionName(str[next])) {
				break;
			}
			next++;
		}
		String firstType = getString(str,startFirstType, next-1);
		if(firstType.equals("void")){
			return true;
		}

		// Now we get one lex unit, go further to check if we get another
		next = skipComment(next, end+1, str);
		if (str[next] == ')') {// this is constructor initializer
			return false;
		}

		return true;
	}

	int[] getFunctionParameters(int endParentheses, char[] str) {
		int[] result = { -1, -1 };
		int pos = getPreMatchingParentheses(endParentheses, str);

		if (pos != -1) {
			if (isFunctionParameters(pos, endParentheses, str)) {

				result[0] = pos;
				result[1] = endParentheses;
			}
		}
		return result;

	}

	int[] getPriviousWord(int start, char[] str) {
		int[] result = { -1, -1 };
		// TODO:

		return result;
	}

	/**
	 * [a-zA-Z] and '_'
	 *
	 * @param c
	 * @return
	 */
	boolean isValidLeadingCharForFunctionName(char c) {
		if ((c > 'a' && c < 'z') || (c > 'A' && c < 'Z') || c == '_') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * [a-zA-Z]
	 *
	 * @param c
	 * @return
	 */
	boolean isChar(char c) {
		if ((c > 'a' && c < 'z') || (c > 'A' && c < 'Z')) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * [a-zA-Z0-9] and '_'
	 *
	 * @param c
	 * @return
	 */
	boolean isValidCharForFunctionName(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * start is the position of the end of the function name, return the
	 * previous position of function name
	 *
	 * example: int .function (int a, int b){...}
	 *
	 * return the position of the '.'
	 *
	 * @param start
	 * @param str
	 * @return
	 */
	int getPreviousInValidCharForFunctionName(int start, char[] str) {

		while (start >= 0 && isValidCharForFunctionName(str[start])) {
			start--;
		}
		// class memeber : class::method
		if (str[start] == ':' && str[start - 1] == ':') {
			start -= 2;
		}

		while (start >= 0 && isValidCharForFunctionName(str[start])) {
			start--;
		}

		if (start < 0) {
			return -1;
		} else {
			return start;
		}

	}

	/**
	 * start is the right ')' Give the right ')', find the left, skip the
	 * comments
	 * 
	 * @param current
	 * @param str
	 * @return
	 */
	int getLeftMatchBrace(int current, char[] str) {
		int next = current;
		while (next >= 0) {
			next = skipCommentBackward(current, str);
			if (next < 0) {
				System.err.println("ERROR: getLeftMatchBrace");
				int lineNum = this.getLineNumber(current);
				System.err.println("Current line: " + lineNum);
				return -1;
			}

			if (str[next] == '(') {
				return next;
			}
			next--;

			next = skipCommentBackward(current, str);
		}
		// Something wrong
		System.err.println("ERROR: getLeftMatchBrace");
		int lineNum = this.getLineNumber(current);
		System.err.println("Current line: " + lineNum);
		return -1;
	}

	/**
	 * try to match targetToMatch at current position
	 * 
	 * @param current
	 * @param str
	 * @param targetToMatch
	 * @return
	 */
	boolean matchPreviousString(int current, char[] str, String targetToMatch) {

		int next = skipCommentBackward(current, str);
		if (next < 0) {
			System.out.println("Info:matchPreviousString: reach the beginning of the code.");
			return false;
		}
		int length = targetToMatch.length();
		int prePos = next - length + 1;
		String word = getString(str, prePos, next);
		if (word.equals(targetToMatch)) {
			return true;
		} else {
			return false;
		}

	}

	int skipNonCharsBackward(int current, char[] str) {
		int next = current;

		while (next >= 0) {
			next = skipCommentBackward(next, str);
			if (isChar(str[next])) {
				return next;
			}
			next--;
		}
		if (next < 0) {
			infoMsg("Info:skipNotCharsBackward: reach the beginning of the code.", false);
			return -1;
		} else {
			errorMsg("Error: skipNotCharsBackward: unexpected case", false);
			return -1;
		}
	}

	/**
	 * start is the starting '(' of the parameters
	 *
	 * int funtion ( int a, int b) {...}
	 *
	 * some trick function declaration:
	 * 
	 * int (function) (int a, int b) {...}
	 * 
	 * void (operator())(int n) { sum += n; }
	 *
	 * @param start
	 * @param str
	 * @return
	 */
	int[] getFunctionName(int start, char[] str) {
		int[] result = { -1, -1 };
		int lineNum = this.getLineNumber(start);

		int current = skipNonCharsBackward(start - 1, str);

		if (current == -1) {
			return result;
		} else {
			// Check if it is operator overload

			// int operator ( ) (int a, int b) {...}

			boolean isOperator = matchPreviousString(current, str, "operator");
			if (isOperator) {

				// Check if it is : int (operator ( )) (int a, int b) {...}

				// get the starting position of opeartor
				int startPos = current - "operator".length() + 1;
				int tryPos = skipCommentBackward(startPos - 1, str);
				if (str[tryPos] == '(') {
					int endPos = getNextMatchingParentheses(tryPos, str.length, str);
					result[0] = startPos;
					result[1] = endPos - 1;

				} else {
					// Current is at last char of "operator"
					int endPos = skipComment(current + 1, str.length, str);

					// Ending char for this operator should be : space or
					// starting comment or '('

					/*
					 * example: int operator <<=// (int a){..}
					 */
					boolean found = false;
					while (endPos < str.length) {
						if (str[endPos] == ' ' || str[endPos] == '\\' || str[endPos] == '(') {
							found = true;
							break;
						}
						endPos++;
					}
					if (found) {
						result[0] = startPos;
						result[1] = endPos - 1;

					} else {
						errorMsg("Unexpected case: getFunctionName", false);
					}
				}

			} else {
				// "Current" is now end of the function name or in middle of the
				// function name
				// example: funname funname_23
				int previousNoeFunNameChar = getPreviousInValidCharForFunctionName(current, str);
				if (previousNoeFunNameChar == -1) {
					errorMsg("Unexpected case: getFunctionName", false);
					return result;
				}

				int endPos = current;
				boolean ok = false;
				while (endPos < str.length) {
					if (!isValidCharForFunctionName(str[endPos])) {
						ok = true;
						break;
					}
					endPos++;
				}
				if (ok) {
					result[0] = previousNoeFunNameChar + 1;
					result[1] = endPos - 1;
				}
			}
		}

		return result;
	}

	private String getString(char[] str, int start, int end) {
		String res = new String(str, start, end - start + 1);
		return res;
	}

	int getCurrentLineStart(int start, char[] str) {
		while (start >= 0 && str[start] != '\n') {
			start--;
		}

		return start + 1;
	}

	int getCurrentLineEnd(int start, int end, char[] str) {
		while (start < end && str[start] != '\n') {
			start--;
		}
		if (start == end) {
			return end - 1;
		} else {
			return start;
		}
	}

	/**
	 * return the // position is it contains "//", otherwise return -1
	 *
	 * @param start
	 * @param end
	 * @param str
	 * @return
	 */
	int hasSingleLineComment(int start, int end, char[] str) {
		int res = -1;

		while (start + 1 < end) {
			if (str[start] == '/' && str[start + 1] == '/') {
				res = start;
				break;
			}
			start++;
		}
		return res;
	}

	/**
	 * Skip consecutive comments forward
	 *
	 * @param start
	 * @param end
	 * @param str
	 * @return
	 */
	private int skipComment(int start, int end, char[] str) {

		int nextStart = start;
		int currentPos = -1;

		// TODO: remove
		int lineNum = this.getLineNumber(start);

		while (nextStart < end) {

			nextStart = skipBlank(nextStart, end, str);
			if (nextStart == -1) {
				return -1;
			}

			currentPos = nextStart;
			int current = nextStart;

			if (str[current] == '/') {
				if (str[current + 1] == '/') {
					current += 2;
					while (current < end && str[current] != '\n') {
						current++;
					}
					if (current < end) {
						nextStart = current + 1;
						continue;
					} else {
						return -1;
					}
				} else if (str[current + 1] == '*') {
					current += 2;
					while (current + 1 < end && !(str[current] == '*' && str[current + 1] == '/')) {
						current++;
					}
					if (current < end) {
						nextStart = current + 2;
						continue;
					} else {
						return -1;
					}

				} else {
					// in c++ "int aa = 2/3;" is valid statement
					// so it is not a error, just treat this is not a comment

				}

			}
			if (nextStart == currentPos) {
				break;
			}
		}

		return currentPos;
	}

	/**
	 * This will skip all consecutive comments backward
	 *
	 * start should be not in middle of the comment for multi line comments or
	 * the second '/'
	 *
	 * Comment 1:
	 *
	 *
	 * @param start
	 * @param str
	 * @return
	 */

	// trick comment can be "//...*/"
	private int skipCommentBackward(int start, char[] str) {

		int nextStart = start;
		int lastPosition = -1;

		while (nextStart >= 0) {
			int current = skipBlankBackward(nextStart, str);

			// After we skip the blank chars, to remember the lastPosition and
			// next start
			// if they are the same after trying to skip comments, then it means
			// there is no comments
			lastPosition = current;
			nextStart = current;
			if (current - 1 < 0) {
				return -1;
			}

			if (str[current] == '/') {
				if (str[current - 1] == '*') {

					// trick comment can be "//...*/", so we need to make sure
					// current "*/" is not part of the comment
					int linestart = getCurrentLineStart(current, str);
					int singleCommentPos = hasSingleLineComment(linestart, current, str);
					if (singleCommentPos >= linestart) {
						nextStart = singleCommentPos - 1;
						continue;
					} else {
						current -= 2;
						while (current - 1 >= 0 && !(str[current - 1] == '/' && str[current] == '*')) {
							current--;
						}
						if (current >= 0) {
							nextStart = current - 2;
							continue;
						} else {
							return -1;
						}
					}

				} else {
					return -1;
				}
			} else {
				int linestart = getCurrentLineStart(current, str);
				int singleCommentPos = hasSingleLineComment(linestart, current, str);
				if (singleCommentPos >= linestart) {
					nextStart = singleCommentPos - 1;
					continue;
				}
			}

			if (lastPosition == nextStart) {
				break;
			}
		}

		return lastPosition;
	}

	boolean isCommentChar(int start, char[] str) {
		if (str[start] == '/') {
			if (str[start + 1] == '/' || str[start + 1] == '*') {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * get next word (just chars, no '_' no digit), now we are using this to get
	 * the "const" after the function parameters: int fun(int a, int b) const
	 * {...}
	 * 
	 * @param start
	 * @param end
	 * @param str
	 * @return
	 */
	String getNextWord(int start, int end, char[] str) {

		String word = null;
		int next = start;
		// skip space & comment
		next = skipComment(next, end, str);

		if (!isChar(str[start]))
			return word;

		while (next < end && isChar(str[next])) {
			next++;
		}
		if (next == end) {
			return word;
		} else {
			word = getString(str, start, next - 1);
			return word;
		}

	}

	/**
	 * if the start is the end of word (just chars), then return the word,
	 * otherwise return null
	 * 
	 * @param start
	 * @param str
	 * @return
	 */
	String getPreviousWord(int start, char[] str) {

		String word = null;
		int next = start;
		// skip space & comment
		next = skipCommentBackward(start, str);

		if (!isChar(str[start]))
			return word;

		while (next >= 0 && isChar(str[next])) {
			next--;
		}
		if (next < 0) {
			return word;
		} else {
			word = getString(str, next + 1, start);
			return word;
		}
	}

	// get next : ")...{"
	int[] getNextFunctionToken(int start, int end, char[] str) {
		int current = start;
		int posEndParenteses = -1;
		int posStartCurlyBracket = -1;
		int[] result = { -1, -1 };

		while (current < end) {

			while (current < end && str[current] != ')') {
				current++;
			}
			if (current >= end) {
				return result;
			}

			// get the position of the end parentheses
			posEndParenteses = current;

			current++;

			// skip space
			int nextChar = skipComment(current, end, str);

			String word = getNextWord(nextChar, end, str);
			if (word != null && word.equals("const")) {
				nextChar += 5;
			}
			nextChar = skipComment(nextChar, end, str);
			if (nextChar == -1) {
				return result;
			} else if (str[nextChar] == '{') {// get the start curl bracket
				posStartCurlyBracket = nextChar;
				break;
			} else {
				// prepare for next while loop
				current = nextChar;
			}
		}

		result[0] = posEndParenteses;
		result[1] = posStartCurlyBracket;
		return result;

	}

	private FunctionDefinition getNextFunctionDefinition(int current, int end, char[] str) {
		FunctionDefinition result = null;
		
		int nextStart = current;
		while (nextStart < end) {
			int[] pos = getNextFunctionToken(nextStart, end, str);
			
			//TODO:debug
			int lineNum = this.getLineNumber(pos[0]);
			
			if (pos[0] == -1 || pos[1] == -1) {
				return result;
			}

			int[] parameters = getFunctionParameters(pos[0], str);
			
			//TODO:debug
			lineNum = this.getLineNumber(pos[0]);
			
			if (parameters[0] == -1 || parameters[1] == -1) {
				nextStart = pos[1]+1;
				continue;
			}
			String parameter = getString(str, parameters[0], parameters[1]);

			int[] functionName = getFunctionName(parameters[0], str);
			if (functionName[0] == -1 || functionName[1] == -1) {
				return result;
			}
			String name = getString(str, functionName[0], functionName[1]);

			int[] body = getFunctionBody(pos[1], end, str);
			if (body[0] == -1 || body[1] == -1) {
				return result;
			}
			
			//TODO:debug
			lineNum = this.getLineNumber(body[0]);			
			lineNum = this.getLineNumber(body[0]);

			String funBody = getString(str, body[0], body[1]);
			result = new FunctionDefinition();
			result.setName(name);
			result.setDefBody(funBody);
			result.setParameter(parameter);
			result.setEndChar(body[1]);
			result.setStartChar(functionName[0]);
			return result;
		}

		return null;

	}

}
