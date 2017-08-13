val list = List(1,2,3,4,5)

val y = for{
  l <- list
} yield l -> l

list.map(l => l -> l)
