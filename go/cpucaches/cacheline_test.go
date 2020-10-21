package cpucaches

import (
	"testing"
)

// sysctl -a | grep cacheline
// hw.cachelinesize: 64
// Each cache line has 64 byte in size


// sysctl hw.l1dcachesize
// hw.l1dcachesize: 32768
// With 64-bit OS, we know this is equal to 512 lines in l1

var arrayLength = 512

// BenchamrkArrayCombination iterating on j. so Array[i] in cacheline
func BenchmarkArrayCombination(b *testing.B) {
	arrayA := create2dArray(arrayLength)
	arrayB := create2dArray(arrayLength)

	for n := 0; n < b.N; n++ {
		for i := 0; i < arrayLength; i++ {
			for j := 0; j < arrayLength; j++ {
				arrayA[i][j] = arrayA[i][j] + arrayB[i][j]
			}
		}
	}
}

// BenchmarkArrayReversedCombination iterating on j. But ArrayB[j] might not be in cacheline
func BenchmarkArrayReversedCombination(b *testing.B) {
	arrayA := create2dArray(arrayLength)
	arrayB := create2dArray(arrayLength)

	for n := 0; n < b.N; n++ {
		for i := 0; i < arrayLength; i++ {
			for j := 0; j < arrayLength; j++ {
				arrayA[i][j] = arrayA[i][j] + arrayB[j][i]
			}
		}
	}
}

// BenchmarkArrayReversedCombinationOptimising iterate on jj. but the blocksize ensure arrayB[jj] still in cacheline
// on 64-bit system, int are 64 bits = 8 bytes
func BenchmarkArrayReversedCombinationOptimising(b *testing.B) {
	arrayA := create2dArray(arrayLength)
	arrayB := create2dArray(arrayLength)
	blockSize := 8

	for n := 0; n < b.N; n++ {
		for i := 0; i < arrayLength; i += blockSize {
			for j := 0; j < arrayLength; j += blockSize {
				for ii := i; ii < i+blockSize; ii++ {
					for jj := j; jj < j+blockSize; jj++ {
						arrayA[ii][jj] = arrayA[ii][jj] + arrayB[jj][ii]
					}
				}
			}
		}
	}
}

func create2dArray(arrayLength int) [][]int {

	Array := make([][]int, arrayLength)

	for i := 0; i < arrayLength; i++ {
		Array[i] = make([]int, arrayLength)
	}

	return Array
}
