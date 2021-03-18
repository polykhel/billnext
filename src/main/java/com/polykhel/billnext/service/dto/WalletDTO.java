package com.polykhel.billnext.service.dto;

import com.polykhel.billnext.domain.enumeration.WalletGroup;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.polykhel.billnext.domain.Wallet} entity.
 */
public class WalletDTO extends AbstractAuditingDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private WalletGroup walletGroup;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal amount;

    private String currency;

    private String remarks;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WalletGroup getWalletGroup() {
        return walletGroup;
    }

    public void setWalletGroup(WalletGroup walletGroup) {
        this.walletGroup = walletGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WalletDTO)) {
            return false;
        }

        WalletDTO walletDTO = (WalletDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, walletDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WalletDTO{" +
            "id=" + getId() +
            ", walletGroup='" + getWalletGroup() + "'" +
            ", name='" + getName() + "'" +
            ", amount=" + getAmount() +
            ", currency='" + getCurrency() + "'" +
            ", remarks='" + getRemarks() + "'" +
            ", user='" + getUser() + "'" +
            "}";
    }
}
