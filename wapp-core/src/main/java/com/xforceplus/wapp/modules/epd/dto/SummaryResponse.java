package com.xforceplus.wapp.modules.epd.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-16 11:41
 **/
@Setter
@Getter
public class SummaryResponse {
    private String taxRateText;
    private int count;
    private double taxRate;
    private boolean isAll;

    public SummaryResponse() {
    }

    public SummaryResponse(int count, String taxRate) {
        this.count = count;
        setTaxRate(taxRate);
    }
    public SummaryResponse(int count, BigDecimal taxRate) {
        this.count = count;
        setTaxRate(taxRate);
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate.doubleValue();
        if (this.taxRate < 1) {
            final BigDecimal multiply = taxRate.multiply(BigDecimal.valueOf(100));
            final BigDecimal scale = multiply.setScale(0);
            if (scale.compareTo(multiply) == 0) {
                this.taxRateText = scale.toPlainString() + "%税率";
            } else {
                this.taxRateText = multiply.toPlainString() + "%税率";
            }
        }
    }
    public void setTaxRate(String taxRatestr) {
        BigDecimal taxRate = new BigDecimal(taxRatestr);
        this.setTaxRate(taxRate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SummaryResponse that = (SummaryResponse) o;
        return count == that.count && Double.compare(that.taxRate, taxRate) == 0 && isAll == that.isAll && Objects.equals(taxRateText, that.taxRateText);
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(taxRateText, count, taxRate, isAll);
    }
}
