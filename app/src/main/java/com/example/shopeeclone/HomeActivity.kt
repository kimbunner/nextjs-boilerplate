package com.example.shopeeclone

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.example.shopeeclone.utils.FirebaseUtils
import kotlinx.coroutines.launch
import com.google.android.material.card.MaterialCardView
import androidx.appcompat.widget.Toolbar

class HomeActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var searchBar: MaterialCardView
    private lateinit var bannerViewPager: ViewPager2
    private lateinit var bannerIndicator: TabLayout
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var flashSaleRecyclerView: RecyclerView
    private lateinit var popularProductsRecyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupToolbar()
        setupBottomNavigation()
        setupRecyclerViews()
        loadData()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        searchBar = findViewById(R.id.searchBar)
        bannerViewPager = findViewById(R.id.bannerViewPager)
        bannerIndicator = findViewById(R.id.bannerIndicator)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        flashSaleRecyclerView = findViewById(R.id.flashSaleRecyclerView)
        popularProductsRecyclerView = findViewById(R.id.popularProductsRecyclerView)
        bottomNav = findViewById(R.id.bottomNav)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        searchBar.setOnClickListener {
            // TODO: Navigate to search activity
        }
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_categories -> {
                    // TODO: Navigate to categories
                    true
                }
                R.id.navigation_cart -> {
                    // TODO: Navigate to cart
                    true
                }
                R.id.navigation_notifications -> {
                    // TODO: Navigate to notifications
                    true
                }
                R.id.navigation_profile -> {
                    // TODO: Navigate to profile
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerViews() {
        // Setup Categories RecyclerView
        categoriesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@HomeActivity, 4)
            // TODO: Set adapter
        }

        // Setup Flash Sale RecyclerView
        flashSaleRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            // TODO: Set adapter
        }

        // Setup Popular Products RecyclerView
        popularProductsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@HomeActivity, 2)
            // TODO: Set adapter
        }
    }

    private fun loadData() {
        showLoading(true)
        lifecycleScope.launch {
            try {
                // Load categories
                when (val categoriesResult = FirebaseUtils.safeCall {
                    FirebaseUtils.getCategories()
                }) {
                    is FirebaseUtils.FirebaseResult.Success -> {
                        // TODO: Update categories adapter
                    }
                    is FirebaseUtils.FirebaseResult.Error -> {
                        handleError(categoriesResult.exception)
                    }
                }

                // Load flash sale products
                when (val flashSaleResult = FirebaseUtils.safeCall {
                    FirebaseUtils.getFlashSaleProducts()
                }) {
                    is FirebaseUtils.FirebaseResult.Success -> {
                        // TODO: Update flash sale adapter
                    }
                    is FirebaseUtils.FirebaseResult.Error -> {
                        handleError(flashSaleResult.exception)
                    }
                }

                // Load popular products
                when (val popularResult = FirebaseUtils.safeCall {
                    FirebaseUtils.getPopularProducts()
                }) {
                    is FirebaseUtils.FirebaseResult.Success -> {
                        // TODO: Update popular products adapter
                    }
                    is FirebaseUtils.FirebaseResult.Error -> {
                        handleError(popularResult.exception)
                    }
                }

                showLoading(false)
            } catch (e: Exception) {
                showLoading(false)
                handleError(e)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun handleError(exception: Exception) {
        // TODO: Show error message to user
    }

    override fun onBackPressed() {
        if (bottomNav.selectedItemId == R.id.navigation_home) {
            super.onBackPressed()
        } else {
            bottomNav.selectedItemId = R.id.navigation_home
        }
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}
