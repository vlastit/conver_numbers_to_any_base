package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.pow

fun main() {
    do {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        val input = readLine()!!
        if (input != "/exit") {
            val convertedInput = input.split(" ").map { it.toInt() }.toList()
            val sourceBase = convertedInput[0]
            val targetBase = convertedInput[1]
            handleConversions(sourceBase, targetBase)
        }
    } while (input != "/exit")
}

private fun handleConversions(source: Int, target: Int) {
    do {
        println("Enter number in base $source to convert to base $target (To go back type /back)")
        val input = readLine()!!
        if (input != "/back") {
            val (integerPart, fractionalPart) = parseInput(input)
            val decimalNumber = if (source != 10) {
                convertIntegerPartToDecimal(integerPart, source)
            } else {
                integerPart.toBigInteger()
            }
            val convertedIntPartOfNumber = convertIntegerPartFromDecimal(decimalNumber, target)
            var convertedFractionPartOfNumber = ""
            if (fractionalPart != "") {
                convertedFractionPartOfNumber = if (fractionalPart == ".0") {
                    ".00000"
                } else {
                    val decimalFractionalNumber = if (source != 10) {
                        convertFractionalPartToDecimal(fractionalPart, source)
                    } else {
                        fractionalPart.toBigDecimal()
                    }
                    convertFractionalPartFromDecimal(decimalFractionalNumber, target)
                }
            }
            val result = convertedIntPartOfNumber + convertedFractionPartOfNumber
            println("Conversion result: $result")
        }
    } while (input != "/back")
}

private fun parseInput(input: String): Pair<String, String> {
    var integerPart = ""
    var fractionalPart = ""
    if (input.contains(".")) {
        val parsedNumber = input.split(".").map { it }.toList()
        integerPart = parsedNumber[0]
        fractionalPart = parsedNumber[1]
    } else {
        integerPart = input
    }
    return Pair(integerPart, fractionalPart)
}

private fun convertIntegerPartFromDecimal(number: BigInteger, radix: Int): String {
    val listOfRemainders = mutableListOf<String>()
    var quotient = number
    do {
        listOfRemainders.add(
            0,
            if (radix <= 10) "${quotient % radix.toBigInteger()}" else matchDecimalToLetter((quotient % radix.toBigInteger()).toInt())
        )
        quotient /= radix.toBigInteger()
    } while (quotient > BigInteger.ZERO)
    return listOfRemainders.joinToString("")
}

private fun convertFractionalPartFromDecimal(number: BigDecimal, radix: Int): String {
    val listOfRemainders = mutableListOf<String>()
    var fractionalPart = number
    var counter = 0
    do {
        val result = fractionalPart * radix.toBigDecimal()
        if ("$result".contains(".")) {
            val list = "$result".split(".").map { it }.toList()
            val integerPart = list[0]
            listOfRemainders.add(
                if (radix <= 10) integerPart else matchDecimalToLetter(integerPart.toInt())
            )
            fractionalPart = BigDecimal("0.${list[1]}")
        } else {
            fractionalPart = BigDecimal(0)
            listOfRemainders.add(
                "0"
            )
        }
        counter += 1
    } while (fractionalPart != BigDecimal.ONE && counter < 5)
    return ".${listOfRemainders.joinToString("")}"
}

private fun convertIntegerPartToDecimal(number: String, radix: Int): BigInteger {
    val convertedNumber = number.reversed()
    var sum: BigInteger = BigInteger.ZERO
    for (i in 0..convertedNumber.length - 1) {
        val multiplier: String =
            if (radix <= 10) convertedNumber[i].toString() else transformLetterToDecimal(convertedNumber[i])
        sum += multiplier.toBigInteger() * radix.toDouble().pow(i).toLong().toBigInteger()
    }
    return sum
}

private fun convertFractionalPartToDecimal(number: String, radix: Int): BigDecimal {
    val convertedNumber = number
    var sum: BigDecimal = BigDecimal.ZERO
    for (i in 0..convertedNumber.length - 1) {
        val multiplier: String =
            if (radix <= 10) convertedNumber[i].toString() else transformLetterToDecimal(convertedNumber[i])
        sum += multiplier.toBigDecimal() * radix.toBigDecimal().pow(-(i + 1), MathContext.DECIMAL64)
    }
    return sum.setScale(5, RoundingMode.CEILING)
}

private fun transformLetterToDecimal(number: Char): String {
    return (when (number) {
        in 'a'..'z' -> (number + 35) - 'z'
        else -> number
    }).toString()
}

private fun matchDecimalToLetter(number: Int): String {
    return when (number) {
        in 10..36 -> "${'a' + (number - 10)}"
        else -> "$number"
    }
}
