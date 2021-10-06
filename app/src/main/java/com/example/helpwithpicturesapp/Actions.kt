package com.example.helpwithpicturesapp

class Actions(
                var image: Int,
                var checkBox: Boolean = false,
)


class ActionsList() {
    var listOfActions: MutableList<Actions> = mutableListOf()

        init {


            val brushTeeth = Actions(
                                    R.drawable.borstatander,
                                    false
            )

            val clean = Actions(
                                    R.drawable.clean,
                                    false
            )

            val vacumClean = Actions(

                                    R.drawable.dammsug,
                                    false
            )

            listOfActions.add(brushTeeth)
            listOfActions.add(clean)
            listOfActions.add(vacumClean)

        }
    }

