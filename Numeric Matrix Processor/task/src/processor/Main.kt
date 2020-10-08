package processor

typealias Vector = List<Number>
typealias Matrix = List<Vector>

fun main() {
    run()
}

tailrec fun run() {
    println("1. Add matrices")
    println("2. Multiply matrix to a constant")
    println("3. Multiply matrices")
    println("4. Transpose matrix")
    println("0. Exit")
    when (readLine().orEmpty()) {
        "1" -> {
            printMatrix(
                matrixOperation(
                    { a, b -> sumNumbers(a, b) },
                    readMatrix("first ") ?: return,
                    readMatrix("second ") ?: return
                ) ?: return println("ERROR")
            )

            return run()
        }
        "2" -> {
            printMatrix(multiplyMatrixWith(
                readMatrix().also { println("Enter multiplier:") } ?: return,
                toNumberOrNull(readLine()) ?: return))

            return run()
        }
        "3" -> {
            printMatrix(
                multiplyMatrices(
                    readMatrix("first ") ?: return,
                    readMatrix("second ") ?: return
                ) ?: return println("ERROR"),
                "The multiplication result is:"
            )
            return run()
        }
        "4" -> {
            println("1. Main diagonal")
            println("2. Side diagonal")
            println("3. Vertical line")
            println("4. Horizontal line")
            when (readLine().orEmpty()) {
                "1" -> printMatrix(transposeMainDiagonally(readMatrix() ?: return))
                "2" -> printMatrix(transposeSideDiagonally(readMatrix() ?: return))
                "3" -> printMatrix(transposeByVerticalLine(readMatrix() ?: return))
                "4" -> printMatrix(transposeByHorizontalLine(readMatrix() ?: return))
            }
            return run()
        }
        "0" ->
            return
        else -> {
            return run()
        }
    }
}

val transposeByVerticalLine = { m: Matrix -> m.map { it.reversed() } }
val transposeByHorizontalLine = { m: Matrix -> m.reversed() }
val transposeMainDiagonally =
    { m: Matrix -> List(m[0].size) { rowIndex -> List(m.size) { columnIndex -> m[columnIndex][rowIndex] } } }
val transposeSideDiagonally =
    { m: Matrix -> transposeByVerticalLine(transposeByHorizontalLine(transposeMainDiagonally(m))) }

fun toNumberOrNull(s: String?): Number? {
    return s?.toIntOrNull() ?: s?.toFloatOrNull()
}

fun sumNumbers(a: Number, b: Number): Number {
    if (a is Int && b is Int) {
        return a + b
    }

    return a.toFloat() + b.toFloat()
}

fun multiplyNumbers(a: Number, b: Number): Number {
    if (a is Int && b is Int) {
        return a * b
    }

    return a.toFloat() * b.toFloat()
}

fun readColumnsOrNull(numColumns: Int, prefixMsg: String? = null): Vector? {
    prefixMsg?.let {
        print("$it ")
    }

    return readLine()?.trim()?.split(" ")?.map {
        toNumberOrNull(it) ?: return null
    }?.also { it.size != numColumns && return err("invalid number of columns") }
}

fun readMatrix(matrixPrefix: String? = null): Matrix? {
    return readColumnsOrNull(2, "Enter size of ${matrixPrefix.orEmpty()}matrix:")?.let { sizes ->
        val numRows = sizes[0].toInt()
        val numColumns = sizes[1].toInt()

        println("Enter ${matrixPrefix.orEmpty()}matrix:")
        List(numRows) {
            readColumnsOrNull(numColumns) ?: return null
        }
    }
}

fun printMatrix(matrix: Matrix, introMsg: String? = null) {
    introMsg?.let {
        println(it)
    }

    matrix.forEach { row ->
        println(row.joinToString(" "))
    }
}

fun matrixOperation(operation: (Number, Number) -> Number, vararg matrices: Matrix): Matrix? {
    return matrices.also {
        it.foldIndexed(1) { index, acc, list ->
            if (index != 0) {
                list.size != acc && return null
            }

            list.size
        }
    }.reduce { summed, matrix ->
        summed.mapIndexed { rowIndex, row ->
            row.mapIndexed { entryIndex, entry ->
                operation(entry, matrix[rowIndex][entryIndex])
            }
        }
    }
}

fun multiplyMatrices(vararg matrices: Matrix): Matrix? {
    return matrices.let {
        if (matrices.size != 2) return null

        val firstMatrix = it[0]
        val secondMatrix = it[1]

        if (firstMatrix[0].size != secondMatrix.size) return null

        List(firstMatrix.size) { rowIndex ->
            List(secondMatrix[0].size) { columnIndex ->
                List(firstMatrix[0].size) { 0 }.foldIndexed(0, { index, acc: Number, _ ->
                    sumNumbers(
                        acc,
                        multiplyNumbers(
                            firstMatrix[rowIndex][index],
                            secondMatrix[index][columnIndex]
                        )
                    )
                })
            }
        }
    }
}

fun multiplyMatrixWith(matrix: Matrix, multiplier: Number): Matrix {
    return matrix.map { row ->
        row.map { entry ->
            multiplyNumbers(entry, multiplier)
        }
    }
}

fun <T> err(msg: String?): T? {
    println(msg)

    return null
}