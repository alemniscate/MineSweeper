package minesweeper

/**
0      .   hidden empty
1..8   .   hidden around number
9      .   hidden mine

10     *  mark empty
11..18 *  mark around number
19     *  mark mine

100       /     free hit empty
101..108  1..8  free hit around number
109       x     free hit mine      ->    geme over

win condition
mark mine count + hidden empty count = mine count

Example 3
field[24] = 9
field[28] = 9
field[44] = 9
field[51] = 9
field[73] = 9
7 3
2 4
9 5
7 6
2 9

Example2
field[1] = 9
field[6] = 9
field[8] = 9
field[24] = 9
field[52] = 9
field[56] = 9
field[63] = 9
field[64] = 9
2 1
7 1
9 1
7 3
8 6
3 7
1 8
2 8

Example1
field[6] = 9
field[9] = 9
field[23] = 9
field[46] = 9
field[49] = 9
field[56] = 9
field[60] = 9
field[64] = 9
field[67] = 9
field[75] = 9
7 1
1 2
6 3
2 6
5 6
3 7
7 7
2 8
5 8
4 9
 */

import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)

    val field = IntArray(81)
    println("How many mines do you want on the field?")
    val mines = scanner.nextInt()

    init(mines, field)

//    debugPrintField(field)
    printField(field)

    var finishflag = false
    while (!finishflag) {
        println("Set/unset mines marks or claim a cell as free:")
        val x = scanner.nextInt() - 1
        val y = scanner.nextInt() - 1
        val command = scanner.next()
        val i = y * 9 + x
        var result = true
        if (command == "mine") {
            result = markField(i, field)
        } else if (command == "free") {
            result = freeField(i, field)
        } else {
            continue
        }

        if (!result) {
            println("Already open!")
            continue
        }

        printField(field)

        finishflag = isgameover(mines, field)
    }
}

fun freeField(i: Int, field: IntArray): Boolean {
    if (field[i] > 9) return false

    val value = field[i] + 100
    field[i] = value
    if (value == 100) explore(i, field)
    return true
}

fun explore(i: Int, field: IntArray) {
    val around = Around(i, field)
    around.explorer()
}

fun markField(i: Int, field: IntArray): Boolean {
    val value = field[i]
    val value2 = when (value) {
        in 0..9 -> value + 10
        in 10..19 -> value - 10
        else -> value
    }
    if (value != value2) {
        field[i] = value2
        return true
    } else {
        return false
    }
}

fun init(mines: Int, field: IntArray) {
    val random = kotlin.random.Random(System.currentTimeMillis())

    repeat(mines) {
        var done = false
        while (!done) {
            var i = random.nextInt(0, 80)
            if (field[i] == 0) {
                field[i] = 9
                done = true
            }
        }
    }

    for (i in 0..80) {
        val around = Around(i, field)
        if (field[i] == 0) around.buildHints()
    }
}

class Around(val i: Int, val field: IntArray) {
    val up = i - 9
    val dn = i + 9
    val left = i - 1
    val right = i + 1
    val upleft = i - 10
    val upright = i - 8
    val dnleft = i + 8
    val dnright = i + 10

    val uplimit = 0
    val dnlimit = 80
    val leftlimit = (i / 9) * 9
    val rightlimit = (i / 9) * 9 + 8

    fun buildHints() {
        var hints = 0
        if (up >= uplimit && field[up] == 9) hints++
        if (dn <= dnlimit && field[dn] == 9) hints++
        if (left >= leftlimit && field[left] == 9) hints++
        if (right <= rightlimit && field[right] == 9) hints++
        if (up >= uplimit && left >= leftlimit && field[upleft] == 9) hints++
        if (up >= uplimit && right <= rightlimit && field[upright] == 9) hints++
        if (dn <= dnlimit && left >= leftlimit && field[dnleft] == 9) hints++
        if (dn <= dnlimit && right <= rightlimit && field[dnright] == 9) hints++

        field[i] = hints
    }

    fun explorer() {
        if (up >= uplimit) update(up, field)
        if (dn <= dnlimit) update(dn, field)
        if (left >= leftlimit) update(left, field)
        if (right <= rightlimit) update(right, field)
        if (up >= uplimit && left >= leftlimit) update(upleft, field)
        if (up >= uplimit && right <= rightlimit) update(upright, field)
        if (dn <= dnlimit && left >= leftlimit) update(dnleft, field)
        if (dn <= dnlimit && right <= rightlimit) update(dnright, field)
    }

    private fun update(pos: Int, field: IntArray) {
        if (field[pos] == 0 || field[pos] == 10) {
            field[pos] = 100
            val around = Around(pos, field)
            around.explorer()
        } else if (field[pos] in 1..8) {
            field[pos] += 100
        } else if (field[pos] in 10..18) {
            field[pos] += 90
        }
    }
}

fun printField(field: IntArray) {
    var gameover = false
    for (i in 0..80) {
        if (field[i] == 109) gameover = true
    }

    println(" │123456789│")
    println("—│—————————│")
    for (i in 0..8) {
        print("${i + 1}│")
        for (j in 0..8) {
            var value = field[i * 9 + j]
            if (value == 9 && gameover) value = 109
            print(
                when (value) {
                    in 0..9 -> "."
                    in 10..19 -> "*"
                    100 -> "/"
                    in 101..108 -> value - 100
                    109 -> "X"
                    else -> value
                }
            )
        }
        println("│")
    }
    println("—│—————————│")
}

fun debugPrintField(field: IntArray) {
    println(" │123456789│")
    println("—│—————————│")
    for (i in 0..8) {
        print("${i + 1}│")
        for (j in 0..8) {
            val value = field[i * 9 + j]
            print(value)
        }
        println("│")
    }
    println("—│—————————│")
}

fun isgameover(mines: Int, field: IntArray): Boolean {
    var minecount = 0
    var unknowncount = 0
    for (i in 0..80) {
        if (field[i] < 10) unknowncount++
        if (field[i] == 19) minecount++
        if (field[i] == 109) {
            println("You stepped on a mine and failed!")
            return true
        }
    }

    if (minecount == mines || minecount + unknowncount == mines) {
        println("Congratulations! You found all mines!")
        return true
    } else {
        return false
    }
}