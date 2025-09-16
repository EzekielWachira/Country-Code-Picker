package com.ezzy.ccp.utils

/**
 * Represents a quadruple, or a tuple of four elements.
 *
 * @param A The type of the first element.
 * @param B The type of the second element.
 * @param C The type of the third element.
 * @param D The type of the fourth element.
 * @property first The first element.
 * @property second The second element.
 * @property third The third element.
 * @property fourth The fourth element.
 */
data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
)
