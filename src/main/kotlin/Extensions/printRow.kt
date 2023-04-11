package Extensions

fun  MutableList<String>.printRows(){
    this.forEachIndexed{index,value->
        when (value.length){
            1 -> print("| $value  ")
            2 -> print("| $value ")
            3 -> print("| $value")
        }
        if (index == 8 || index == 17 || index == 26) println("|")
    }
}