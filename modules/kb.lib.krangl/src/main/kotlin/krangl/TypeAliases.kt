package krangl


typealias DataFrameRow = Map<String, Any?>

typealias VectorizedRowPredicate = ExpressionContext.(ExpressionContext) -> BooleanArray

typealias TableExpression = ExpressionContext.(ExpressionContext) -> Any?