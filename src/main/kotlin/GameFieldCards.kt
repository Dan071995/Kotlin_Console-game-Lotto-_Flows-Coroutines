import Extensions.printRows

enum class GameFieldCards{
    //Создаем 6 объектов, хранящих уникальные игровые карты
    GameCard1,
    GameCard2,
    GameCard3,
    GameCard4,
    GameCard5,
    GameCard6;

    //Создаем приватный объект, хранящий список всех возможных значений на полях карточки. При создании 1-ой карточки, из
    //данного списка стираются 15 значений. После создания 6-ти игровых карт данный список пустеет
    private object List { val numbersList = (1..90).toMutableList() }
    //Создаем игровую карту
    private val createGameFieldCard = listOf(createRow(),createRow(),createRow())
    //Создаем копию игровой карты. Это копия будет изменяться во время игры (будут наноситься метки на элементы, совпавшие
    // с ведущим). Таким образом у нас всегда будет храниться первоначальная, не измененная версия игрового поля. Так же
    //изменяемая версия преобразуется из двумерного массива в одномерный, для простоты работы с ним.
    val currentGameCard = createGameFieldCard.flatten().toMutableList()

    //Функция вывода текущего игрового поля на экран
    fun printGameCard(){
        val definePlayers = when(this.name.last()){
            '1' -> "1"
            '2' -> "1"
            '3' -> "1"
            '4' -> "2"
            '5' -> "2"
            '6' -> "2"
            else -> "Unknown"
        }
        println("        ~~ Game Card № ${this.name.last()} (Player $definePlayers) ~~")
        currentGameCard.printRows()
        println()
    }
    //Функция создания игровой строки
    private fun createRow():MutableList<String>{
        //создаем строку с 9-ю пустыми клетками (элементами)
        val row = MutableList(9){" "}
        //Добавляем в строку 5 рандомных элементов и убираем их из общего пула значений
        repeat(5){
            val x = List.numbersList.random()
            List.numbersList.remove(x)
            row[it] = (x.toString())
        }
        row.shuffle() //Расставляем элементы массива в рандомном порядке
        //row.sort()
        return row
    }
}
