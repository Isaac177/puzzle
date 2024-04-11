import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.npuzzle.MainActivity
import com.example.npuzzle.R
import android.widget.Button


class MainFragment : Fragment() {

    private val logTag = "MainFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(logTag, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_main, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mainActivity: MainActivity = activity as MainActivity

        // Manually finding views by ID
        val buttonThreeXThree = view?.findViewById<Button>(R.id.button_three_x_three)
        val buttonFourXFour = view?.findViewById<Button>(R.id.button_four_x_four)
        val buttonFiveXFive = view?.findViewById<Button>(R.id.button_five_x_five)

        buttonThreeXThree?.setOnClickListener {
            mainActivity.showPuzzleFragment(3)
        }
        /*buttonFourXFour?.setOnClickListener {
            mainActivity.showPuzzleFragment(4)
        }
        buttonFiveXFive?.setOnClickListener {
            mainActivity.showPuzzleFragment(5)
        }*/
    }
}
