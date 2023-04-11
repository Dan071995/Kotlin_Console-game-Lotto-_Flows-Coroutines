import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

object Generator {

    //Создаем объект генератор, который достает из мешка боченок (рандомное число от 1 до 90). Обычный flow (в отличие
    // от горячих flow) конечен и его не нужно отменять

    val regularFlow = (1..90).shuffled().asFlow().onEach { delay(250) }


}