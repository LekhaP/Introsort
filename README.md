# Introsort (Sort huge adress file)

Introsort or introspective sort is a hybrid sorting algorithm that provides both fast average performance and (asymptotically) optimal worst-case performance. It begins with quicksort and switches to heapsort when the recursion depth exceeds a level based on (the logarithm of) the number of elements being sorted. This combines the good parts of both algorithms, with practical performance comparable to quicksort on typical data sets and worst-case O(n log n) runtime due to the heap sort. Since both algorithms it uses are comparison sorts, it too is a comparison sort.

This project implements introsort with combination of insertionsort/quicksort/heapsort. Appropriate algorithm/ combination_of_algorithm is used based on the size of input data for better performance.**Nbfjdghijghdfjhk
