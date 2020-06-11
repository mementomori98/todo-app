package mox.todo.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.navigation.ui.AppBarConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import mox.todo.app.R
import mox.todo.app.ui.fragments.SettingsFragment
import mox.todo.app.ui.fragments.TodosFragment
import mox.todo.app.ui.viewmodels.MainViewModel
import kotlin.reflect.KClass

class MainActivity : ActivityBase() {

    private val allTodosId = 1000000
    private var listId: Int? = null

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        setupNavigation()
        loadFragment(TodosFragment())
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        when(item.itemId) {
            allTodosId -> {
                listId = null
                loadFragment(TodosFragment())
            }
            R.id.nav_settings -> loadFragment(SettingsFragment())
            R.id.nav_new_list -> loadActivity(CreateListActivity::class)
            else -> {
                listId = item.itemId
                loadFragment(TodosFragment(listId))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(this, "Hello World from options", LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()

        reinitializeTodoListItems()
    }

    private fun loadFragment(fragment: Fragment) {
        title = "Todos"
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_content, fragment)
        transaction.commit()
    }

    private fun<T> loadActivity(activity: KClass<T>, bundle: Bundle? = null) where T : Any {
        val intent = Intent(
            applicationContext,
            activity.java
        )
        bundle?.let(intent::putExtras)
        startActivity(intent)
    }

    private fun setupNavigation() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val bundle = Bundle()
            listId?.let { bundle.putInt("listId", it) }
            loadActivity(CreateTodoActivity::class, bundle)
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_header_title, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected)

        reinitializeTodoListItems()
    }

    private fun reinitializeTodoListItems() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val menu = navigationView.menu
        menu.removeGroup(R.id.menu_todo_lists)
        menu.add(R.id.menu_todo_lists, allTodosId, Menu.NONE, "All Todos")
        viewModel.todoLists().value.forEach {
            menu.add(R.id.menu_todo_lists, it.id, it.id + 1, it.name)
        }
    }

}