package connectfour

import java.util.*
import kotlin.system.exitProcess

class Player(val name: String, var marker: Char) {
    var score: Int = 0
}

class GameState(
    var moveNumber: Int,
    val player1: Player,
    val player2: Player,
    var board: Array<CharArray>
)

fun main() {
    println("Connect Four")

    val firstPlayer = Player(askPlayerInfo("First player's name:"), 'o')
    val secondPlayer = Player(askPlayerInfo("Second player's name:"), '*')
    val boardSize = askAboutDimensions()
    var numberOfGames = askAboutNumOfGames()
    val gameState = GameState(1, firstPlayer, secondPlayer, makeBoard(boardSize))

    while (numberOfGames == 0) {
        println("Invalid input")
        numberOfGames = askAboutNumOfGames()
    }

    println("${firstPlayer.name} VS ${secondPlayer.name}\n${boardSize.first} X ${boardSize.second} board")
    println(if (numberOfGames > 1) "Total $numberOfGames games" else "Single game")

    for (i in 1..numberOfGames) {
        if (numberOfGames > 1) println("Game #$i")
        printBoard(gameState.board)
        startGame(gameState, gameState.board)
        gameState.board = makeBoard(boardSize)
    }
    exitGame()
}

fun askPlayerInfo(message: String): String {
    println(message)
    return readLine().toString()
}

fun askAboutDimensions(): Pair<Int, Int> {
    println(
        "Set the board dimensions (Rows x Columns)\n" +
                "Press Enter for default (6 x 7)"
    )

    val input = readLine().toString().replace("\\s".toRegex(), "").lowercase(Locale.getDefault())

    return if (input.isEmpty()) {
        Pair(6, 7)
    } else {
        val x: Int
        val y: Int

        if (input.matches(Regex("[0-9]+x[0-9]+"))) {
            x = Character.getNumericValue(input.first())
            y = Character.getNumericValue(input.last())
            if (x !in 5..9) {
                println("Board rows should be from 5 to 9")
                askAboutDimensions()
            } else if (y !in 5..9) {
                println("Board columns should be from 5 to 9")
                askAboutDimensions()
            } else {
                Pair(x, y)
            }
        } else {
            println("Invalid input")
            askAboutDimensions()
        }
    }
}

fun askAboutNumOfGames(): Int {
    println(
        "Do you want to play single or multiple games?\n" +
                "For a single game, input 1 or press Enter\n" +
                "Input a number of games:"
    )

    val input = readLine().toString()

    return if (input == "") {
        1
    } else if (input.toIntOrNull() != null && input.toInt() != 0) {
        input.toInt()
    } else 0
}

fun startGame(gameState: GameState, board: Array<CharArray>) {
    while (true) {
        if (playerMove(gameState)) {
            gameState.moveNumber++
            return
        }
        printBoard(board)
        gameState.moveNumber++
    }
}

fun playerMove(gameState: GameState): Boolean {

    val movePlayer = if (gameState.moveNumber % 2 != 0) gameState.player1 else gameState.player2
    val waitPlayer = if (gameState.moveNumber % 2 != 0) gameState.player2 else gameState.player1

    println("${movePlayer.name}'s turn:")
    val command = readLine().toString()

    if (command == "end") {
        exitGame()
    } else {

        val column = command.toIntOrNull()
        var busyCells = 0

        if (column != null && column !in 1..gameState.board[0].size / 2) {
            println("The column number is out of range (1 - ${gameState.board[0].size / 2})")
            playerMove(gameState)
        } else if (column != null && column in 1..gameState.board[0].size) {
            for (i in gameState.board.size - 1 downTo 0) {
                if (i > 0 && i < gameState.board.size - 1 && gameState.board[i][column + (column - 1)] == ' ') {
                    gameState.board[i][column + (column - 1)] = movePlayer.marker
                    if (!gameState.board.drop(1).joinToString(separator = "") { it.joinToString(separator = "") }
                            .contains(' ')) {
                        printBoard(gameState.board)
                        println("It is a draw")
                        movePlayer.score += 1
                        waitPlayer.score += 1
                        finishGame(gameState)
                        return true
                    }
                    if (checkWinningCondition(gameState.board, movePlayer)) {
                        movePlayer.score += 2
                        finishGame(gameState)
                        return true
                    }

                    return false
                }
                busyCells++
                if (busyCells > gameState.board.size - 2) {
                    println("Column $column is full")
                    playerMove(gameState)
                    return false
                }
            }
        } else {
            println("Incorrect column number")
            playerMove(gameState)
        }
    }
    return false
}

fun checkWinningCondition(board: Array<CharArray>, player: Player): Boolean {

    for (i in board.indices) {
        for (j in board[i].indices) {
            when {
                // Vertical check
                board[i][j] == player.marker && board[i + 1][j] == player.marker && board[i + 2][j] == player.marker && board[i + 3][j] == player.marker -> {
                    printBoard(board)
                    println("Player ${player.name} won")
                    return true
                }
                // Horizontal check
                j < board[i].size - 7 && board[i][j] == player.marker && board[i][j + 2] == player.marker && board[i][j + 4] == player.marker && board[i][j + 6] == player.marker -> {
                    printBoard(board)
                    println("Player ${player.name} won")
                    return true
                }
                // Diagonal check
                j > 6 && board[i][j] == player.marker && board[i + 1][j - 2] == player.marker && board[i + 2][j - 4] == player.marker && board[i + 3][j - 6] == player.marker -> {
                    printBoard(board)
                    println("Player ${player.name} won")
                    return true
                }
                j < board[i].size - 7 && board[i][j] == player.marker && board[i + 1][j + 2] == player.marker && board[i + 2][j + 4] == player.marker && board[i + 3][j + 6] == player.marker -> {
                    printBoard(board)
                    println("Player ${player.name} won")
                    return true
                }
            }
        }
    }
    return false
}

fun finishGame(gameState: GameState) {
    println("Score \n${gameState.player1.name}: ${gameState.player1.score} ${gameState.player2.name}: ${gameState.player2.score}")
}

fun exitGame() {
    println("Game over!")
    exitProcess(0)
}

fun makeBoard(boardSize: Pair<Int, Int>): Array<CharArray> {

    val (rows, columns) = boardSize
    val board = Array(rows + 2) { CharArray(columns * 2 + 1) }

    for (i in board.indices) {
        when (i) {
            0 -> board[i] = (" " + (1..columns).joinToString(" ")).toCharArray()
            board.lastIndex -> board[i] = ("╚" + ("═╩".repeat(columns - 1)) + "═╝").toCharArray()
            else -> board[i] = ("║" + (" ║".repeat(columns - 1)) + " ║").toCharArray()
        }
    }
    return board
}

fun printBoard(board: Array<CharArray>) {
    board.forEach { println(it) }
}