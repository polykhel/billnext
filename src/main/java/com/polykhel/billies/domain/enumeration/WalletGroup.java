package com.polykhel.billies.domain.enumeration;

/**
 * The WalletGroup enumeration.
 */
public enum WalletGroup {
    CASH("Cash"),
    ACCOUNTS("Accounts"),
    CARD("Card"),
    DEBIT("Debit Card"),
    SAVINGS("Savings"),
    PREPAID("Top-Up/Prepaid"),
    INVESTMENTS("Investments"),
    OVERDRAFTS("Overdrafts"),
    LOAN("Loan"),
    INSURANCE("Insurance"),
    OTHERS("Others");

    private final String value;

    WalletGroup(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
