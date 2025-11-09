# 🎨 Sort Visualizer Pro

A modern, feature-rich sorting algorithm visualizer built with Java Swing.

## ✨ Features

### **Single Mode**

- Visualize 12 different sorting algorithms
- Real-time statistics (comparisons, swaps, elapsed time)
- Adjustable array size (10-400 elements)
- Adjustable animation speed (1-100)
- Optional sound feedback
- Beautiful gradient visualizations
- Status indicators (Ready → Sorting → Sorted)

### **⚖ Comparison Mode** _(NEW!)_

- **Compare two algorithms side-by-side**
- Same input data for fair comparison
- Independent tracking for each algorithm
- Real-time winner detection
- Detailed statistics comparison:
  - Time elapsed
  - Number of comparisons
  - Number of swaps
- Visual result display showing which algorithm performed better

### **Supported Algorithms**

1. **Bubble Sort** - O(n²)
2. **Selection Sort** - O(n²)
3. **Insertion Sort** - O(n²)
4. **Merge Sort** - O(n log n)
5. **Quick Sort** - O(n log n) average
6. **Heap Sort** - O(n log n)
7. **Shell Sort** - O(n log n)
8. **Cocktail Sort** - O(n²)
9. **Comb Sort** - O(n²/2ᵖ)
10. **Gnome Sort** - O(n²)
11. **Radix Sort** - O(nk)
12. **Counting Sort** - O(n+k)

## 🚀 How to Run

### Prerequisites

- Java 11 or higher
- Terminal/Command Prompt

### Compilation & Execution

```bash
# Compile all Java files
javac *.java

# Run the application
java SortVisualizer
```

## 📖 How to Use

### Single Algorithm Mode

1. **Select Algorithm**: Choose from the dropdown menu
2. **Adjust Settings**:
   - Array Size: Control the number of elements (10-400)
   - Speed: Adjust animation speed (1-100)
   - Sound: Toggle audio feedback on/off
3. **Click "▶ Start"**: Begin visualization
4. **Click "⏹ Stop"**: Stop at any time

### Comparison Mode

1. **Click "⚖ Compare Mode"** button to switch modes
2. **Select Two Algorithms**:
   - Choose Algorithm 1 from the first dropdown
   - Choose Algorithm 2 from the second dropdown
3. **Adjust Settings**: Array size and speed apply to both
4. **Click "▶ Start"**: Both algorithms run simultaneously
5. **View Results**: Winner is automatically determined and displayed

### Understanding the Visualization

**Bar Colors:**

- 🔵 **Blue/Purple**: Unsorted elements
- 🔴 **Red**: Current comparison element (A)
- 🟢 **Green**: Current comparison element (B)
- 🟢 **Teal**: Sorted elements

**Status Indicators:**

- ○ **Ready**: Algorithm is ready to start
- ● **Sorting**: Algorithm is currently running
- ✓ **Sorted**: Algorithm has completed

## 🎯 Comparison Mode Tips

### Best Comparisons to Try:

1. **Fast vs Slow**:

   - Quick Sort vs Bubble Sort
   - Merge Sort vs Selection Sort

2. **Similar Complexity**:

   - Quick Sort vs Merge Sort
   - Insertion Sort vs Selection Sort

3. **Different Approaches**:

   - Radix Sort vs Quick Sort (non-comparative vs comparative)
   - Counting Sort vs Merge Sort

4. **Watch the Statistics**:
   - Some algorithms make fewer comparisons but more swaps
   - Some are faster but use more comparisons
   - Time, comparisons, and swaps all tell different parts of the story!

## 🎨 UI Features

- **Modern Design**: Clean, professional interface with smooth animations
- **Dark Visualization Area**: High contrast for better visibility
- **Gradient Bars**: Beautiful color gradients for each bar
- **Real-time Stats**: Live updates of comparisons, swaps, and time
- **Responsive Controls**: Smooth hover effects and immediate feedback
- **Grid Background**: Subtle grid for reference
- **Information Panels**: Compact stats in comparison mode
- **Result Display**: Clear winner announcement with detailed metrics

## 📊 File Structure

```
SortVisualizer/
├── SortVisualizer.java        # Main entry point
├── SortVisualizerFrame.java   # Main window & UI
├── BarPanel.java              # Visualization panel
├── SortingAlgorithms.java     # Algorithm implementations
└── ToneGenerator.java         # Sound generation
```

## 🛠 Technical Details

- **Language**: Java 11+
- **UI Framework**: Swing
- **Threading**: Multi-threaded for smooth animations
- **Sound**: MIDI-based audio feedback
- **Architecture**: MVC pattern with observer updates

## 💡 Performance Notes

- Larger arrays (200+) may take longer to sort with O(n²) algorithms
- Speed setting affects delay between operations
- Sound is disabled in comparison mode for better performance
- Both algorithms in comparison mode run independently

## 🐛 Troubleshooting

**No sound?**

- MIDI may not be available on your system
- The visualizer will work without sound

**Slow performance?**

- Reduce array size
- Increase speed setting
- Disable sound

**Can't switch modes?**

- Stop any running sorting first
- Mode changes are disabled during sorting

## 📝 License

This is an educational project for learning sorting algorithms and Java GUI programming.

## 🌟 Enjoy visualizing sorting algorithms!

Compare, learn, and understand how different algorithms perform under the same conditions!
