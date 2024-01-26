package org.bookstore;

enum ActionType{
    UPDATE_BOOK_DETAILS(1),
    LIST_BOOKS_BY_GENRE(2),
    LIST_BOOKS_BY_AUTHOR(3),
    UPDATE_CUSTOMERS_INFO(4),
    CUSTOMERS_PURCHASE_HISTORY(5),
    REVENUE_BY_GENRE(6),
    PROCESS_NEW_SALE(7),
    SOLD_BOOK_REPORT(8),
    REVENUE_BY_GENRE_REPORT(9),
    EXIT(0);

    private int value;

    private ActionType(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }

    static ActionType fromValue(int value) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.getValue() == value) {
                return actionType;
            }
        }
        return null;
    }
}
