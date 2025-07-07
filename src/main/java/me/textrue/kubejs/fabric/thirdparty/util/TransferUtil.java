package me.textrue.kubejs.fabric.thirdparty.util;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class TransferUtil {
	/**
	 * @return Either an outer transaction or a nested one in the current open one
	 */
	public static Transaction getTransaction() {
		if (Transaction.isOpen()) {
			//noinspection deprecation
			TransactionContext open = Transaction.getCurrentUnsafe();
			if (open != null) {
				return open.openNested();
			}
		}
		return Transaction.openOuter();
	}

}
