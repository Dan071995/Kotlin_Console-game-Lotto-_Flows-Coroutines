import Extensions.isTrue
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

object Game {

    init { //Выводим правила игры
        val gameRules = File("data/game rules.txt").readText().split("\n").joinToString("\n")
        println(gameRules)
    }

    fun play() {

        val k = valueOfGameCardsForPlayers() //Просим пользователя ввести кол-во карт у каждого игрока

        //Сохраняем изменяемую игровую карточку в новую переменную и убираем пустые ячейки. Далее мы будем осуществлять
        //поиск элементов в этой переменной и удалять из нее найденные значения. Когда данный список опустеет -> игрок победил
        //(все числа на игровой карточке закрыты т.е удалены из списка). Удалять найденные числа из текущего списка
        //будем для того, чтобы ускорить последующий поиск
        val player1Cards = mutableListOf(GameFieldCards.GameCard1.currentGameCard.filter { it != " " }.toMutableList())
        when(k){
            2-> player1Cards += GameFieldCards.GameCard2.currentGameCard.filter { it != " " }.toMutableList()
            3-> {player1Cards += GameFieldCards.GameCard2.currentGameCard.filter { it != " " }.toMutableList()
                 player1Cards += GameFieldCards.GameCard3.currentGameCard.filter { it != " " }.toMutableList() }
        }

        val player2Cards = mutableListOf(GameFieldCards.GameCard4.currentGameCard.filter { it != " " }.toMutableList())

        when(k){
            2-> player2Cards += GameFieldCards.GameCard5.currentGameCard.filter { it != " " }.toMutableList()
            3-> {player2Cards += GameFieldCards.GameCard5.currentGameCard.filter { it != " " }.toMutableList()
                player2Cards += GameFieldCards.GameCard6.currentGameCard.filter { it != " " }.toMutableList() }
        }

        val playersWithCards = listOf(player1Cards,player2Cards) //создаем список с игроками

        //Выводим карточки игроков на экран
        printPlayersCards(playersWithCards,k)

        val notMissChance = notMissChance()  // Просим пользователя ввести вероятность НЕ пропустить нужный боченок у игроков
        //при значении 100 игроки не пропускают боченки, при значении 0 игроки всегда пропускают боченки

        runBlocking {
            //Когда один из игроков победил, флаг меняется на true и происходит отписка во всех лоддекторах
            var flag = false

            //ПОДПИСЧИК 1 запустим генератор чисел (холодный источник)
            launch {
                var i = 1
                Generator.regularFlow
                    .collect {
                        if (flag) cancel() //производим отписку если флаг true (т.е какой-то из игроков победил)
                        if (!flag) println("Barrel №:$it (round №: ${i++})") //Выводим информацию об извлеченном боченке на экран
                        if (i == 91 && !flag) println("No one vin!") //Если ни один из игроков не поднял флаг, а боченки в мешок закончились, печатаем что никто не победил
                    }
            }
        //ЗАПУСК НОВЫХ ПОДПИСЧИКОВ (ИГРОКОВ) ИЗ СПИСКА ИГРОКОВ
        playersWithCards.forEachIndexed { index, currentPlayer ->
            launch {
                Generator.regularFlow
                    .collect{

                        for (i in 0 until k) {

                            if (flag) { //Отписываемся от источника, какой-то из игроков поднял флаг
                                cancel()
                                return@collect
                            }
                            //Если в карточке игрока нашелся элемент нужный номер, добавляем к этому номеру символ !
                            if (currentPlayer[i].contains(it.toString())) {
                                if (notMissChance.isTrue()) { //Вероятность пропустить нужный боченок
                                    currentPlayer[i] -= it.toString() //удаляем найденный элемент из коллекции для ускорения поиска

                                    //добавляем к совпавшему элементу символ !
                                    when (index) {
                                    0 -> {
                                        println()
                                        when (i) {
                                            0 -> { GameFieldCards.GameCard1.currentGameCard[GameFieldCards.GameCard1.currentGameCard.indexOf(it.toString())] += "!"
                                                   GameFieldCards.GameCard1.printGameCard() }
                                            1 -> { GameFieldCards.GameCard2.currentGameCard[GameFieldCards.GameCard2.currentGameCard.indexOf(it.toString())] += "!"
                                                   GameFieldCards.GameCard2.printGameCard() }
                                            2 -> { GameFieldCards.GameCard3.currentGameCard[GameFieldCards.GameCard3.currentGameCard.indexOf(it.toString())] += "!"
                                                   GameFieldCards.GameCard3.printGameCard() }
                                        }
                                    }
                                    1 -> {
                                        println()
                                        when (i) {
                                            0 -> { GameFieldCards.GameCard4.currentGameCard[GameFieldCards.GameCard4.currentGameCard.indexOf(it.toString())] += "!"
                                                   GameFieldCards.GameCard4.printGameCard() }
                                            1 -> { GameFieldCards.GameCard5.currentGameCard[GameFieldCards.GameCard5.currentGameCard.indexOf(it.toString())] += "!"
                                                   GameFieldCards.GameCard5.printGameCard() }
                                            2 -> { GameFieldCards.GameCard6.currentGameCard[GameFieldCards.GameCard6.currentGameCard.indexOf(it.toString())] += "!"
                                                   GameFieldCards.GameCard6.printGameCard() }
                                        }
                                    }
                                    else -> error("To mach player!")
                                }

                                }

                                if (currentPlayer[i].isEmpty() || flag) { //Отписываемся от источника, если карточка игрока заполнилась
                                    flag = true
                                    cancel()
                                    if (currentPlayer[i].isEmpty()) {
                                        when (index){
                                            0 -> println("The winner is: player1 !!")
                                            1 -> println("The winner is: player2 !!")
                                            else -> println("The winner is: UNKNOWN")
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }
        }
    }


    private fun valueOfGameCardsForPlayers():Int {

        print("Choose the number of game cards for each player ( from 1 to 3 ) = ")
        var n: Int?

        do {
            n = readLine()?.toIntOrNull()
            if (n == null || n < 1 || n > 3) println("Incorrect Value, PLAYERS CAN HAVE FROM 1 TO 3 CARDS!")

        } while (n == null || n < 1 || n > 3)

        return n
    }

    private fun notMissChance():Int {

        print("Choose the probability of a player NOT missing a number (0 - 100%) = ")
        var n: Int?

        do {
            n = readLine()?.toIntOrNull()
            if (n == null || n < 0 || n > 100) println("Incorrect Value, enter value from 0 to 100!")

        } while (n == null || n < 0 || n > 100)

        return n
    }

    //Функция печати карточек игроков
    private fun printPlayersCards(playersWithCards: List<MutableList<MutableList<String>>>, k:Int){
        playersWithCards.forEachIndexed { index, playerCardsList ->

            for (i in 0 until k) {
                if (index == 0) {
                    when (i) {
                        0 -> { GameFieldCards.GameCard1.printGameCard() }
                        1 -> { GameFieldCards.GameCard2.printGameCard() }
                        2 -> { GameFieldCards.GameCard3.printGameCard() }
                    }
                }
                else{
                    when (i) {
                        0 -> { GameFieldCards.GameCard4.printGameCard() }
                        1 -> { GameFieldCards.GameCard5.printGameCard() }
                        2 -> { GameFieldCards.GameCard6.printGameCard() }
                    }
                }
            }
        }
    }


}