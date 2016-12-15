package com.zeyad.usecases.codegen;

/**
 * @author zeyad on 12/12/16.
 */
class JavaScanner {

    private final String s;

    JavaScanner(String s) {
        if (!s.endsWith("\n")) {
            s += "\n";
            // This allows us to avoid checking for the end of the string in most cases.
        }
        this.s = s;
    }

    int tokenEnd(int start) {
        if (start >= s.length()) {
            return s.length();
        }
        switch (s.charAt(start)) {
            case ' ':
            case '\n':
                return spaceEnd(start);
            case '/':
                if (s.charAt(start + 1) == '*') {
                    return blockCommentEnd(start);
                } else if (s.charAt(start + 1) == '/') {
                    return lineCommentEnd(start);
                } else {
                    return start + 1;
                }
            case '\'':
            case '"':
            case '`':
                return quoteEnd(start);
            default:
                // Every other character is considered to be its own token.
                return start + 1;
        }
    }

    private int spaceEnd(int start) {
        assert s.charAt(start) == ' ' || s.charAt(start) == '\n';
        int i;
        for (i = start + 1; i < s.length() && s.charAt(i) == ' '; i++) {
        }
        return i;
    }

    private int blockCommentEnd(int start) {
        assert s.charAt(start) == '/' && s.charAt(start + 1) == '*';
        int i;
        for (i = start + 2; s.charAt(i) != '*' || s.charAt(i + 1) != '/'; i++) {
        }
        return i + 2;
    }

    private int lineCommentEnd(int start) {
        assert s.charAt(start) == '/' && s.charAt(start + 1) == '/';
        int end = s.indexOf('\n', start + 2);
        assert end > 0;
        return end;
    }

    private int quoteEnd(int start) {
        char quote = s.charAt(start);
        assert quote == '\'' || quote == '"' || quote == '`';
        int i;
        for (i = start + 1; s.charAt(i) != quote; i++) {
            if (s.charAt(i) == '\\') {
                i++;
            }
        }
        return i + 1;
    }
}
