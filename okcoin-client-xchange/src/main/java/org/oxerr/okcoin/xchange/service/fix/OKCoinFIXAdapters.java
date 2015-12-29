package org.oxerr.okcoin.xchange.service.fix;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.oxerr.okcoin.fix.fix44.AccountInfoResponse;

import com.xeiam.xchange.currency.Currency;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.account.Balance;
import com.xeiam.xchange.dto.account.Wallet;

import quickfix.FieldNotFound;
import quickfix.Message;

/**
 * Various adapters for converting from {@link Message} to XChange DTOs.
 */
public final class OKCoinFIXAdapters {

	private OKCoinFIXAdapters() {
	}

	public static String adaptSymbol(CurrencyPair currencyPair) {
		return String.format("%s/%s", currencyPair.base.getCurrencyCode(), currencyPair.counter.getCurrencyCode());
	}

	public static AccountInfo adaptAccountInfo(AccountInfoResponse message)
			throws FieldNotFound {
		final String[] currencies = message.getCurrency().getValue().split("/");
		final String[] balances = message.getBalance().getValue().split("/");

		final int walletCount = currencies.length;
		final List<Balance> balanceList = new ArrayList<>(walletCount);

		for (int i = 0; i < walletCount; i++) {
			final String currency = currencies[i];
			final BigDecimal available = new BigDecimal(balances[i]);
			final Balance balance = new Balance(Currency.getInstance(currency), available, available);
			balanceList.add(balance);
		}

		final Wallet wallet = new Wallet(balanceList);
		return new AccountInfo(wallet);
	}

}
