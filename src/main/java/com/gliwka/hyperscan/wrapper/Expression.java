package com.gliwka.hyperscan.wrapper;

import com.gliwka.hyperscan.jni.hs_compile_error_t;
import com.gliwka.hyperscan.jni.hs_expr_info_t;
import lombok.*;

import java.util.EnumSet;

import static com.gliwka.hyperscan.jni.hyperscan.*;


/**
 * Expression to be compiled as a Database and then be used for scanning using the Scanner
 */
@EqualsAndHashCode
@ToString
public class Expression {
    @Getter @NonNull private final String expression;
    @Getter private EnumSet<ExpressionFlag> flags = EnumSet.of(ExpressionFlag.NO_FLAG);
    @Getter private Integer id;

    /**
     * Represents the validation results for a expression
     */
    public static class ValidationResult {
        @Getter private String errorMessage;
        @Getter private final boolean isValid;

        ValidationResult(boolean isValid) {
            this.isValid = isValid;
        }

        ValidationResult(String errorMessage, boolean isValid) {
            this.errorMessage = errorMessage;
            this.isValid = isValid;
        }
    }

    public Expression(@NonNull String expression) {
        this.expression = expression;
    }

    public Expression(@NonNull String expression, Integer id) {
        this.expression = expression;
        this.id = id;
    }

    public Expression(@NonNull String expression, @NonNull EnumSet<ExpressionFlag> flags) {
        this.expression = expression;
        this.flags = flags;
    }

    public Expression(@NonNull String expression, @NonNull ExpressionFlag flag) {
        this.expression = expression;
        this.flags = EnumSet.of(flag);
    }

    public Expression(@NonNull String expression, @NonNull EnumSet<ExpressionFlag> flags, Integer id) {
        this.expression = expression;
        this.flags = flags;
        this.id = id;
    }

    public Expression(@NonNull String expression, @NonNull ExpressionFlag flag, Integer id) {
        this.expression = expression;
        this.flags = EnumSet.of(flag);
        this.id = id;
    }

    public ValidationResult validate() {
        try(hs_expr_info_t info = new hs_expr_info_t(); hs_compile_error_t error = new hs_compile_error_t()) {
            int hsResult = hs_expression_info(this.expression, getFlagBits(), info, error);

            if(hsResult != 0) {
                return new ValidationResult(error.message().getString(), false);

            }
            else {
                return new ValidationResult(true);
            }

        }
    }

    int getFlagBits() {
        int bitValue = 0;

        if(flags != null) {
            for(BitFlag flag : flags) {
                bitValue = flag.getBits() | bitValue;
            }
        }

        return bitValue;
    }
}