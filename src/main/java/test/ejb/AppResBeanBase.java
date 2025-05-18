package test.ejb;

import jakarta.annotation.PreDestroy;

public class AppResBeanBase implements AppResCommonIF {
    protected StringBuilder postConstructRecords = new StringBuilder();

    protected String myString;

    public StringBuilder getPostConstructRecords() {
        return postConstructRecords;
    }

    public String getName() {
        return null;
    }

    @SuppressWarnings("unused")
    @PreDestroy
    private void preDestroy() {
        System.out.println("In preDestroy of " + this);
    }
}
