package com.assignment.aop.adapter;

/**
 * This interface defines the contract for any data transformation between SQL and NoSQL.
 *
 * @author Ansh Parnami
 * @since 2026-02-26
 */
public interface DataSyncAdapter<T, S> {

  T sync(S s);
}
