package com.newerty.derivedStats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class DerivedExpressionEvaluatorVisitor extends DerivedExpressionBaseVisitor<EvaluationFunction> {

    private static final Logger LOG = LoggerFactory.getLogger(DerivedExpressionEvaluatorVisitor.class);

    private final String categoryId;

    public DerivedExpressionEvaluatorVisitor(String categoryId) {
        this.categoryId = categoryId;
    }

    // A = B
    @Override
    public EvaluationFunction visitAssignment(DerivedExpressionParser.AssignmentContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => assignContext:: {}", ctx.getText());
        }
        return visit(ctx.expression());
    }

    // A + B
    @Override
    public EvaluationFunction visitAdditivePLUS(DerivedExpressionParser.AdditivePLUSContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => AdditivePLUSContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.additive_expression());
        EvaluationFunction right = visit(ctx.multiplicative_expression());
        return (values, ph) -> left.apply(values, ph).add(right.apply(values, ph));
    }

    // A - B
    @Override
    public EvaluationFunction visitAdditiveMINUS(DerivedExpressionParser.AdditiveMINUSContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => AdditiveMINUSContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.additive_expression());
        EvaluationFunction right = visit(ctx.multiplicative_expression());
        return (values, ph) -> left.apply(values, ph).subtract(right.apply(values, ph));
    }

    // A  B
    @Override
    public EvaluationFunction visitMultiplicativeMULTI(DerivedExpressionParser.MultiplicativeMULTIContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => MultiplicativeMULTIContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.multiplicative_expression());
        EvaluationFunction right = visit(ctx.unary_expression());
        return (values, ph) -> left.apply(values, ph).multiply(right.apply(values, ph));
    }

    // A / B
    @Override
    public EvaluationFunction visitMultiplicativeDIV(DerivedExpressionParser.MultiplicativeDIVContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => MultiplicativeDIVContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.multiplicative_expression());
        EvaluationFunction right = visit(ctx.unary_expression());
        return (values, ph) -> left.apply(values, ph).divide(right.apply(values, ph));
    }

    // NUMBER
    @Override
    public EvaluationFunction visitDecimalLiteral(DerivedExpressionParser.DecimalLiteralContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => DecimalLiteralContext:: {}", ctx.getText());
        }
        double value = Double.valueOf(ctx.DECIMAL().getText());
        return (values, ph) -> new EvaluationContext(value);
    }

    // +INF
    @Override
    public EvaluationFunction visitPosINF(DerivedExpressionParser.PosINFContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => PosINFContext:: {}", ctx.getText());
        }
        return (values, ph) -> new EvaluationContext(Double.POSITIVE_INFINITY);
    }

    // -INF
    @Override
    public EvaluationFunction visitNegInf(DerivedExpressionParser.NegInfContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => NegInfContext:: {}", ctx.getText());
        }
        return (values, ph) -> new EvaluationContext(Double.NEGATIVE_INFINITY);
    }

    // NaN
    @Override
    public EvaluationFunction visitNaN(DerivedExpressionParser.NaNContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => NaNContext:: {}", ctx.getText());
        }
        return (values, ph) -> new EvaluationContext(Double.NaN);
    }

    // ( primary )
    @Override
    public EvaluationFunction visitParens(DerivedExpressionParser.ParensContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => ParensContext:: {}", ctx.getText());
        }
        return visit(ctx.expression());
    }

    // (unary) -NUMBER
    @Override
    public EvaluationFunction visitMINUSPrimary(DerivedExpressionParser.MINUSPrimaryContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => MINUSPrimaryContext:: {}", ctx.getText());
        }
        EvaluationFunction arg = visit(ctx.primary_expression());
        return (values, ph) -> arg.apply(values, ph).negate();
    }

    // (unary) +NUMBER
    @Override
    public EvaluationFunction visitPLUSPrimary(DerivedExpressionParser.PLUSPrimaryContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => PLUSPrimaryContext:: {}", ctx.getText());
        }
        return visit(ctx.primary_expression());
    }

    // A && B
    @Override
    public EvaluationFunction visitLogicalAND(DerivedExpressionParser.LogicalANDContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => LogicalANDContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.logical_and_expression());
        EvaluationFunction right = visit(ctx.equality_expression());
        return (values, ph) -> left.apply(values, ph).logicalAnd(right.apply(values, ph));
    }

    // A || B
    @Override
    public EvaluationFunction visitLogicalOR(DerivedExpressionParser.LogicalORContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => LogicalORContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.logical_or_expression());
        EvaluationFunction right = visit(ctx.logical_and_expression());
        return (values, ph) -> left.apply(values, ph).logicalOr(right.apply(values, ph));
    }

    // A > B
    @Override
    public EvaluationFunction visitRelationalGT(DerivedExpressionParser.RelationalGTContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => RelationalGTContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.relational_expression());
        EvaluationFunction right = visit(ctx.additive_expression());
        return (values, ph) -> left.apply(values, ph).relationalGT(right.apply(values, ph));
    }

    // A >= B
    @Override
    public EvaluationFunction visitRelationalGE(DerivedExpressionParser.RelationalGEContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => RelationalGEContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.relational_expression());
        EvaluationFunction right = visit(ctx.additive_expression());
        return (values, ph) -> left.apply(values, ph).relationalGTE(right.apply(values, ph));
    }

    // A < B
    @Override
    public EvaluationFunction visitRelationalLT(DerivedExpressionParser.RelationalLTContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => RelationalLTContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.relational_expression());
        EvaluationFunction right = visit(ctx.additive_expression());
        return (values, ph) -> left.apply(values, ph).relationalLT(right.apply(values, ph));
    }

    // A <= B
    @Override
    public EvaluationFunction visitRelationalLE(DerivedExpressionParser.RelationalLEContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => RelationalLEContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.relational_expression());
        EvaluationFunction right = visit(ctx.additive_expression());
        return (values, ph) -> left.apply(values, ph).relationalLTE(right.apply(values, ph));
    }

    // A == B
    @Override
    public EvaluationFunction visitEqualityEQ(DerivedExpressionParser.EqualityEQContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => EqualityEQContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.equality_expression());
        EvaluationFunction right = visit(ctx.relational_expression());
        return (values, ph) -> left.apply(values, ph).relationalEQ(right.apply(values, ph));
    }

    // A != B
    @Override
    public EvaluationFunction visitEqualityNEQ(DerivedExpressionParser.EqualityNEQContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => EqualityNEQContext:: {}", ctx.getText());
        }
        EvaluationFunction left = visit(ctx.equality_expression());
        EvaluationFunction right = visit(ctx.relational_expression());
        return (values, ph) -> left.apply(values, ph).relationalNEQ(right.apply(values, ph));
    }

    // SUM(A,B,C...)
    @Override
    public EvaluationFunction visitSUMAggregate(DerivedExpressionParser.SUMAggregateContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => SUMAggregateContext:: {}", ctx.getText());
        }

        List<EvaluationFunction> args = ctx.aggregateList().aggregateClause()
                .expression()
                .stream()
                .map(this::visit)
                .collect(Collectors.toList());

        return (values, ph) -> new EvaluationContext(args.stream()
                .flatMapToDouble(arg -> arg.apply(values, ph).asStream())
                .sum());
    }

    // AVG(A,B,C...)
    @Override
    public EvaluationFunction visitAVGAggregate(DerivedExpressionParser.AVGAggregateContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => AVGAggregateContext:: {}", ctx.getText());
        }

        List<EvaluationFunction> args = ctx.aggregateList().aggregateClause()
                .expression()
                .stream()
                .map(this::visit)
                .collect(Collectors.toList());

        return (values, ph) -> new EvaluationContext(args.stream()
                .flatMapToDouble(arg -> arg.apply(values, ph).asStream())
                .average()
                .orElse(0d));
    }

    // MAX(A,B,C...)
    @Override
    public EvaluationFunction visitMAXAggregate(DerivedExpressionParser.MAXAggregateContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => MAXAggregateContext:: {}", ctx.getText());
        }

        List<EvaluationFunction> args = ctx.aggregateList().aggregateClause()
                .expression()
                .stream()
                .map(this::visit)
                .collect(Collectors.toList());

        return (values, ph) -> new EvaluationContext(args.stream()
                .flatMapToDouble(arg -> arg.apply(values, ph).asStream())
                .max()
                .orElse(0d));
    }

    // MIN(A,B,C...)
    @Override
    public EvaluationFunction visitMINAggregate(DerivedExpressionParser.MINAggregateContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => MINAggregateContext:: {}", ctx.getText());
        }

        List<EvaluationFunction> args = ctx.aggregateList().aggregateClause()
                .expression()
                .stream()
                .map(this::visit)
                .collect(Collectors.toList());

        return (values, ph) -> new EvaluationContext(args.stream()
                .flatMapToDouble(arg -> arg.apply(values, ph).asStream())
                .min()
                .orElse(0d));
    }

    // A ? B : C
    @Override
    public EvaluationFunction visitTernary(DerivedExpressionParser.TernaryContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => TernaryContext:: {}", ctx.getText());
        }

        EvaluationFunction arg = visit(ctx.parent.getChild(0));
        EvaluationFunction left = visit(ctx.expression(0));
        EvaluationFunction right = visit(ctx.expression(1));

        return (values, ph) -> {
            if (arg.apply(values, ph).getValue() > 0) {
                return left.apply(values, ph);
            } else {
                return right.apply(values, ph);
            }
        };
    }

    @Override
    public EvaluationFunction visitStatId(DerivedExpressionParser.StatIdContext ctx) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(" => StatIdContext:: {}", ctx.getText());
        }

        ExpressionStatId dependentStatId = new ExpressionStatIdBuilder()
                .from(ctx, categoryId)
                .build();

        if (dependentStatId.isWildcarded()) {
            return (values, ph) -> {
                if (ph.isEmpty()) {
                    // if no placeholders, assume this is a stream for aggregation
                    DoubleStream v = DoubleStream.empty();

                    // TODO: optimize this to not do a linear scan, at least pass in a sorted map and then bisect
                    for (Map.Entry<ExpressionStatId, Double> e : values.entrySet()) {
                        if (dependentStatId.keysEquals(e.getKey())) {
                            v = DoubleStream.concat(v, DoubleStream.of(e.getValue()));
                        }
                    }

                    return new EvaluationContext(v);
                } else {
                    // otherwise just extract the matching dimension
                    ExpressionStatId target = dependentStatId.withDimensions(ph);

                    return new EvaluationContext(values.getOrDefault(target, 0d));
                }
            };
        } else {
            // simple case, we just return the exact value from the map
            return (values, ph) -> {
                if (values == null) {
                    return new EvaluationContext(0d);
                }

                return new EvaluationContext(values.getOrDefault(dependentStatId, 0d));
            };
        }
    }
}
