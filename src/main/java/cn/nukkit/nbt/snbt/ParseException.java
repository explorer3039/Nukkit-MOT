package cn.nukkit.nbt.snbt;

import java.util.*;


@SuppressWarnings("serial")
public class ParseException extends RuntimeException implements SNBTConstants {
    // The token we tripped up on.
    private Token token;
    //We were expecting one of these token types
    private EnumSet<TokenType> expectedTypes;
    private List<SNBTParserImplement.NonTerminalCall> callStack;
    private boolean alreadyAdjusted;
    private SNBTParserImplement parser;

    private void setInfo(SNBTParserImplement parser, Token token, EnumSet<TokenType> expectedTypes, List<SNBTParserImplement.NonTerminalCall> callStack) {
        this.parser = parser;
        if (token != null && token.getType() != TokenType.EOF && token.getNext() != null) {
            token = token.getNext();
        }
        this.token = token;
        this.expectedTypes = expectedTypes;
        this.callStack = new ArrayList<>(callStack);
    }

    public boolean hitEOF() {
        return token != null && token.getType() == TokenType.EOF;
    }

    public ParseException(SNBTParserImplement parser, Token token, EnumSet<TokenType> expectedTypes, List<SNBTParserImplement.NonTerminalCall> callStack) {
        setInfo(parser, token, expectedTypes, callStack);
    }

    public ParseException(SNBTParserImplement parser, String message) {
        super(message);
        setInfo(parser, parser.lastConsumedToken, null, parser.parsingStack);
    }

    public ParseException(SNBTParserImplement parser, EnumSet<TokenType> expectedTypes, List<SNBTParserImplement.NonTerminalCall> callStack) {
        this(parser, parser.lastConsumedToken, expectedTypes, callStack);
    }

    public ParseException(Token token) {
        this.token = token;
    }

    // Needed because of inheritance
    public ParseException() {
        super();
    }

    // Needed because of inheritance
    public ParseException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (token == null && expectedTypes == null) {
            return msg;
        }
        StringBuilder buf = new StringBuilder();
        if (msg != null)
            buf.append(msg);
        buf.append("\nEncountered an error at (or somewhere around) " + token.getLocation());
        if (expectedTypes != null && token != null && expectedTypes.contains(token.getType())) {
            return buf.toString();
        }
        if (expectedTypes != null) {
            buf.append("\nWas expecting one of the following:\n");
            boolean isFirst = true;
            for (TokenType type : expectedTypes) {
                if (!isFirst)
                    buf.append(", ");
                isFirst = false;
                buf.append(type);
            }
        }
        String content = token.getImage();
        if (content == null)
            content = "";
        if (content.length() > 32)
            content = content.substring(0, 32) + "...";
        buf.append("\nFound string \"" + SNBTLexer.addEscapes(content) + "\" of type " + token.getType());
        return buf.toString();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        adjustStackTrace();
        return super.getStackTrace();
    }

    @Override
    public void printStackTrace(java.io.PrintStream s) {
        adjustStackTrace();
        super.printStackTrace(s);
    }

    /**
     * Returns the token which causes the parse error and null otherwise.
     *
     * @return the token which causes the parse error and null otherwise.
     */
    public Token getToken() {
        return token;
    }

    public SNBTParserImplement getParser() {
        return parser;
    }

    private void adjustStackTrace() {
        if (alreadyAdjusted || callStack == null || callStack.isEmpty()) return;
        List<StackTraceElement> fullTrace = new LinkedList<>();
        List<StackTraceElement> ourCallStack = new LinkedList<>();
        for (SNBTParserImplement.NonTerminalCall ntc : callStack) {
            ourCallStack.add(ntc.createStackTraceElement());
        }
        StackTraceElement[] jvmCallStack = super.getStackTrace();
        for (StackTraceElement regularEntry : jvmCallStack) {
            if (ourCallStack.isEmpty()) break;
            String methodName = regularEntry.getMethodName();
            StackTraceElement ourEntry = lastElementWithName(ourCallStack, methodName);
            if (ourEntry != null) {
                fullTrace.add(ourEntry);
            }
            fullTrace.add(regularEntry);
        }
        StackTraceElement[] result = new StackTraceElement[fullTrace.size()];
        setStackTrace(fullTrace.toArray(result));
        alreadyAdjusted = true;
    }

    private StackTraceElement lastElementWithName(List<StackTraceElement> elements, String methodName) {
        for (ListIterator<StackTraceElement> it = elements.listIterator(elements.size()); it.hasPrevious(); ) {
            StackTraceElement elem = it.previous();
            if (elem.getMethodName().equals(methodName)) {
                it.remove();
                return elem;
            }
        }
        return null;
    }

}

