package ru.geographer29.polytech.labs.factorization;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PollardRhoOneMethod extends AbstractFactorization {

    private List<BigInteger> bs = new ArrayList<>();
    private List<BigInteger> ls = new ArrayList<>();
    private double LN = Math.log(Math.E);
    private double valLog = ln(val);
    private long launchTime = System.currentTimeMillis();
    private BigInteger a, d = one, l = one;

    public PollardRhoOneMethod(BigInteger val) {
        super(val);
    }

    @Override
    public String call() throws Exception {
        BigInteger r = rhoOne();

        printHeader(r);

        if (r.compareTo(one) == 0)
            sb.append("Factor has not computed, planned termination\n");
        else if (r.compareTo(minusOne) == 0)
            sb.append("Factor has not found\n");
        else
            sb.append("Factor is ").append(r).append("\nChecking ").append(val).append(" = ")
                    .append(r).append(" * ").append(val.divide(r)).append("\n");

        printTime();
        return sb.toString();
    }

    private BigInteger rhoOne() {
        fillInFactorBase(new BigInteger("100000000"));

        for (;;) {
            if (System.currentTimeMillis() - launchTime > 3600000)
                break;

            a = getRandomBase();
            d = a.gcd(val);

            if (d.compareTo(two) != -1)
                return d;

            for (BigInteger prime: bs) {
                l = new BigDecimal( valLog / ln(prime)).toBigInteger();
                ls.add(l);
                a = a.modPow(prime.pow(l.intValue()), val);
                d = a.subtract(one).gcd(val);
            }

            if (d.equals(one) || d.equals(val))
                return minusOne;
            else
                return d;
        }

        return d;
    }

    private double ln(BigInteger val) {
        int blex = val.bitLength() - 1022; // any value in 60..1023 is ok

        if (blex > 0)
            val = val.shiftRight(blex);

        double res = Math.log(val.doubleValue());

        return blex > 0 ? res + blex * LN : res;
    }

    private void printHeader(BigInteger r) {
        sb.append("Computation using ")
                .append(getClass().getSimpleName())
                .append("\nValue = ").append(val)
                .append("\nPrinting decomposition bases\n");

        for (BigInteger num : bs)
            sb.append("B[")
                    .append(iteration++)
                    .append("] = ")
                    .append(num)
                    .append("\n");

        iteration = 1;

        for (BigInteger num : ls)
            sb.append("L[")
                    .append(iteration++)
                    .append("] = ")
                    .append(num)
                    .append("\n");

    }

    private void printTime() {
        sb
                .append("Total iterations = ")
                .append(iteration)
                .append("\nComputation time = ")
                .append(System.currentTimeMillis() - launchTime)
                .append(" ms\n");
    }

    void fillInFactorBase (BigInteger lastItem) {
        for (BigInteger i = two; i.compareTo(lastItem) != 1;) {
            if (i.compareTo(lastItem) == 1)
                break;

            bs.add(i);
            i = i.nextProbablePrime();
        }
    }

}
