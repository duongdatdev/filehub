<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
    <!-- Enhanced Header with Breadcrumb -->
    <div class="bg-white border-b border-gray-200 shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Breadcrumb -->
        <nav class="flex pt-4 pb-2" aria-label="Breadcrumb">
          <ol role="list" class="flex items-center space-x-2">
            <li>
              <router-link to="/admin" class="text-gray-400 hover:text-gray-500 transition-colors">
                <svg class="flex-shrink-0 h-4 w-4" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M9.293 2.293a1 1 0 011.414 0l7 7A1 1 0 0117 11h-1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-3a1 1 0 00-1-1H9a1 1 0 00-1 1v3a1 1 0 01-1 1H5a1 1 0 01-1-1v-6H3a1 1 0 01-.707-1.707l7-7z" clip-rule="evenodd" />
                </svg>
              </router-link>
            </li>
            <li>
              <div class="flex items-center">
                <svg class="flex-shrink-0 h-4 w-4 text-gray-300" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                </svg>
                <span class="ml-2 text-sm font-medium text-gray-900">Departments</span>
              </div>
            </li>
          </ol>
        </nav>

        <!-- Header Content -->
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between py-6">
          <div class="flex-1 min-w-0">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <div class="w-12 h-12 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg">
                  <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                  </svg>
                </div>
              </div>
              <div class="ml-4">
                <h1 class="text-3xl font-bold leading-tight text-gray-900 sm:text-4xl">
                  Department Management
                </h1>
                <p class="mt-2 text-sm text-gray-600 max-w-2xl">
                  Create, organize, and manage your organization's departments and hierarchical structure
                </p>
              </div>
            </div>
          </div>
          <div class="mt-4 sm:mt-0 sm:ml-4 flex flex-col sm:flex-row gap-3">
            <button
              @click="refreshData"
              :disabled="loading"
              class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-lg shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
            >
              <svg :class="['w-4 h-4 mr-2', loading ? 'animate-spin' : '']" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              {{ loading ? 'Refreshing...' : 'Refresh' }}
            </button>
            <button
              @click="showCreateModal = true"
              class="inline-flex items-center px-6 py-2 border border-transparent rounded-lg shadow-lg text-sm font-medium text-white bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transform hover:scale-105 transition-all duration-200"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
              </svg>
              Create Department
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">

      <!-- Enhanced Stats Cards with Loading States -->
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4 mb-8">
        <!-- Total Departments Card -->
        <div class="group bg-white rounded-xl shadow-soft hover:shadow-medium transition-all duration-300 overflow-hidden border border-gray-100">
          <div class="p-6">
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <div class="w-12 h-12 bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                    <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                    </svg>
                  </div>
                </div>
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-500">Total Departments</p>
                  <p class="text-2xl font-bold text-gray-900">
                    <span v-if="loading" class="inline-block w-12 h-8 bg-gray-200 animate-pulse rounded"></span>
                    <span v-else class="bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">{{ stats.totalDepartments }}</span>
                  </p>
                </div>
              </div>
              <div class="text-green-500 text-sm font-medium">
                <svg class="w-4 h-4 inline" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M5.293 9.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 7.414V15a1 1 0 11-2 0V7.414L6.707 9.707a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
          </div>
          <div class="bg-gradient-to-r from-blue-50 to-indigo-50 px-6 py-3 border-t border-blue-100">
            <router-link to="/admin/departments" class="text-sm font-medium text-blue-600 hover:text-blue-800 transition-colors">
              View all departments →
            </router-link>
          </div>
        </div>

        <!-- Total Users Card -->
        <div class="group bg-white rounded-xl shadow-soft hover:shadow-medium transition-all duration-300 overflow-hidden border border-gray-100">
          <div class="p-6">
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <div class="w-12 h-12 bg-gradient-to-r from-emerald-500 to-emerald-600 rounded-lg flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                    <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                  </div>
                </div>
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-500">Total Users</p>
                  <p class="text-2xl font-bold text-gray-900">
                    <span v-if="loading" class="inline-block w-12 h-8 bg-gray-200 animate-pulse rounded"></span>
                    <span v-else class="bg-gradient-to-r from-emerald-600 to-green-600 bg-clip-text text-transparent">{{ stats.totalUsers }}</span>
                  </p>
                </div>
              </div>
              <div class="text-green-500 text-sm font-medium">
                <svg class="w-4 h-4 inline" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M5.293 9.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 7.414V15a1 1 0 11-2 0V7.414L6.707 9.707a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
          </div>
          <div class="bg-gradient-to-r from-emerald-50 to-green-50 px-6 py-3 border-t border-emerald-100">
            <router-link to="/admin/users" class="text-sm font-medium text-emerald-600 hover:text-emerald-800 transition-colors">
              Manage users →
            </router-link>
          </div>
        </div>

        <!-- Active Projects Card -->
        <div class="group bg-white rounded-xl shadow-soft hover:shadow-medium transition-all duration-300 overflow-hidden border border-gray-100">
          <div class="p-6">
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <div class="w-12 h-12 bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                    <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                    </svg>
                  </div>
                </div>
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-500">Active Projects</p>
                  <p class="text-2xl font-bold text-gray-900">
                    <span v-if="loading" class="inline-block w-12 h-8 bg-gray-200 animate-pulse rounded"></span>
                    <span v-else class="bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">{{ stats.activeProjects }}</span>
                  </p>
                </div>
              </div>
              <div class="text-green-500 text-sm font-medium">
                <svg class="w-4 h-4 inline" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M5.293 9.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 7.414V15a1 1 0 11-2 0V7.414L6.707 9.707a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
          </div>
          <div class="bg-gradient-to-r from-purple-50 to-pink-50 px-6 py-3 border-t border-purple-100">
            <router-link to="/admin/projects" class="text-sm font-medium text-purple-600 hover:text-purple-800 transition-colors">
              View projects →
            </router-link>
          </div>
        </div>

        <!-- Active Departments Card -->
        <div class="group bg-white rounded-xl shadow-soft hover:shadow-medium transition-all duration-300 overflow-hidden border border-gray-100">
          <div class="p-6">
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <div class="w-12 h-12 bg-gradient-to-r from-amber-500 to-orange-600 rounded-lg flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                    <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                </div>
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-500">Active Departments</p>
                  <p class="text-2xl font-bold text-gray-900">
                    <span v-if="loading" class="inline-block w-12 h-8 bg-gray-200 animate-pulse rounded"></span>
                    <span v-else class="bg-gradient-to-r from-amber-600 to-orange-600 bg-clip-text text-transparent">{{ departmentStats.activeDepartments }}</span>
                  </p>
                </div>
              </div>
              <div class="text-green-500 text-sm font-medium">
                <svg class="w-4 h-4 inline" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M5.293 9.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 7.414V15a1 1 0 11-2 0V7.414L6.707 9.707a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
          </div>
          <div class="bg-gradient-to-r from-amber-50 to-orange-50 px-6 py-3 border-t border-amber-100">
            <span class="text-sm font-medium text-amber-600">{{ Math.round((departmentStats.activeDepartments / stats.totalDepartments) * 100) || 0 }}% active rate</span>
          </div>
        </div>
      </div>

      <!-- Enhanced Filters Section -->
      <div class="bg-white rounded-xl shadow-soft border border-gray-100 mb-8">
        <div class="px-6 py-4 border-b border-gray-100">
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <svg class="w-5 h-5 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.414A1 1 0 013 6.707V4z" />
              </svg>
              <h3 class="text-lg font-semibold text-gray-900">Filters & Search</h3>
            </div>
            <button
              @click="clearFilters"
              class="text-sm text-gray-500 hover:text-gray-700 font-medium transition-colors"
            >
              Clear all
            </button>
          </div>
        </div>
        <div class="p-6">
          <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
            <!-- Department Name Filter -->
            <div class="space-y-2">
              <label for="name-filter" class="block text-sm font-semibold text-gray-700">Department Name</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                </div>
                <input
                  id="name-filter"
                  v-model="filters.name"
                  type="text"
                  placeholder="Search departments..."
                  class="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-gray-50 focus:bg-white"
                />
              </div>
            </div>

            <!-- Manager Filter -->
            <div class="space-y-2">
              <label for="manager-filter" class="block text-sm font-semibold text-gray-700">Manager</label>
              <select
                id="manager-filter"
                v-model="filters.managerId"
                class="block w-full py-3 px-4 border border-gray-300 bg-gray-50 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 focus:bg-white"
              >
                <option value="">All Managers</option>
                <option v-for="manager in managers" :key="manager.id" :value="manager.id">
                  {{ manager.fullName }}
                </option>
              </select>
            </div>

            <!-- Status Filter -->
            <div class="space-y-2">
              <label for="status-filter" class="block text-sm font-semibold text-gray-700">Status</label>
              <select
                id="status-filter"
                v-model="filters.isActive"
                class="block w-full py-3 px-4 border border-gray-300 bg-gray-50 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 focus:bg-white"
              >
                <option value="">All Statuses</option>
                <option :value="true">Active</option>
                <option :value="false">Inactive</option>
              </select>
            </div>

            <!-- Apply Filters Button -->
            <div class="flex items-end">
              <button
                @click="applyFilters"
                :disabled="loading"
                class="w-full inline-flex justify-center items-center py-3 px-6 border border-transparent shadow-sm text-sm font-medium rounded-lg text-white bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transform hover:scale-105 transition-all duration-200"
              >
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
                Apply Filters
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Enhanced Department Table -->
      <div class="bg-white rounded-xl shadow-soft border border-gray-100 overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-100">
          <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h3 class="text-lg font-semibold text-gray-900">Departments</h3>
              <p class="mt-1 text-sm text-gray-500">
                Showing {{ departments.length }} of {{ stats.totalDepartments }} departments
              </p>
            </div>
            <div class="mt-4 sm:mt-0 flex items-center space-x-3">
              <!-- View Toggle -->
              <div class="flex items-center bg-gray-100 rounded-lg p-1">
                <button
                  @click="viewMode = 'table'"
                  :class="[
                    'px-3 py-1 text-sm font-medium rounded-md transition-all duration-200',
                    viewMode === 'table' 
                      ? 'bg-white text-gray-900 shadow-sm' 
                      : 'text-gray-500 hover:text-gray-700'
                  ]"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M3 6h18m-9 8h9m-9 4h9" />
                  </svg>
                </button>
                <button
                  @click="viewMode = 'grid'"
                  :class="[
                    'px-3 py-1 text-sm font-medium rounded-md transition-all duration-200',
                    viewMode === 'grid' 
                      ? 'bg-white text-gray-900 shadow-sm' 
                      : 'text-gray-500 hover:text-gray-700'
                  ]"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Table View -->
        <div v-if="viewMode === 'table'" class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Department
                </th>
                <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Manager
                </th>
                <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <div class="flex items-center">
                    <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    Users
                  </div>
                </th>
                <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <div class="flex items-center">
                    <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                    </svg>
                    Projects
                  </div>
                </th>
                <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="department in departments" :key="department.id" class="hover:bg-gray-50 transition-colors duration-200 group">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="flex-shrink-0 h-12 w-12">
                      <div class="h-12 w-12 rounded-xl bg-gradient-to-r from-blue-500 to-indigo-600 flex items-center justify-center shadow-lg group-hover:scale-105 transition-transform duration-200">
                        <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                        </svg>
                      </div>
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-semibold text-gray-900">{{ department.name }}</div>
                      <div class="text-sm text-gray-500" v-if="department.description">
                        {{ department.description.length > 50 ? department.description.substring(0, 50) + '...' : department.description }}
                      </div>
                      <div class="text-xs text-gray-400 mt-1" v-if="department.parentId">
                        Parent: {{ getParentDepartmentName(department.parentId) }}
                      </div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="h-8 w-8 bg-gray-100 rounded-full flex items-center justify-center mr-3">
                      <svg class="h-4 w-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                      </svg>
                    </div>
                    <div>
                      <div class="text-sm font-medium text-gray-900">{{ getManagerName(department.managerId) }}</div>
                      <div class="text-xs text-gray-500" v-if="department.managerId">Manager</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="text-sm font-semibold text-gray-900">{{ department.userCount || 0 }}</div>
                    <div class="ml-2 px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full">
                      {{ (department.userCount || 0) !== 1 ? 'users' : 'user' }}
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="text-sm font-semibold text-gray-900">{{ department.projectCount || 0 }}</div>
                    <div class="ml-2 px-2 py-1 text-xs bg-purple-100 text-purple-800 rounded-full">
                      {{ (department.projectCount || 0) !== 1 ? 'projects' : 'project' }}
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    :class="[
                      'inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold',
                      department.isActive
                        ? 'bg-green-100 text-green-800 border border-green-200'
                        : 'bg-red-100 text-red-800 border border-red-200'
                    ]"
                  >
                    <div
                      :class="[
                        'w-1.5 h-1.5 rounded-full mr-2',
                        department.isActive ? 'bg-green-400' : 'bg-red-400'
                      ]"
                    ></div>
                    {{ department.isActive ? 'Active' : 'Inactive' }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div class="flex items-center space-x-2">
                    <!-- Action Dropdown -->
                    <div class="relative inline-block text-left dropdown-container">
                      <button
                        @click="toggleDropdown(department.id)"
                        class="inline-flex items-center p-2 text-gray-400 hover:text-gray-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 rounded-lg hover:bg-gray-100 transition-all duration-200"
                      >
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z" />
                        </svg>
                      </button>
                      
                      <!-- Dropdown Menu -->
                      <div
                        v-if="activeDropdown === department.id"
                        class="origin-top-right absolute right-0 mt-2 w-48 rounded-lg shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none z-10"
                      >
                        <div class="py-1">
                          <button
                            @click="viewDepartment(department)"
                            class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                          >
                            <svg class="w-4 h-4 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                            </svg>
                            View Details
                          </button>
                          <button
                            @click="editDepartment(department)"
                            class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                          >
                            <svg class="w-4 h-4 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                            Edit Department
                          </button>
                          <button
                            @click="manageDepartmentUsers(department)"
                            class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                          >
                            <svg class="w-4 h-4 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                            </svg>
                            Manage Users
                          </button>
                          <hr class="my-1">
                          <button
                            @click="toggleDepartmentStatus(department)"
                            :class="[
                              'flex items-center w-full px-4 py-2 text-sm transition-colors',
                              department.isActive 
                                ? 'text-red-600 hover:bg-red-50' 
                                : 'text-green-600 hover:bg-green-50'
                            ]"
                          >
                            <svg v-if="department.isActive" class="w-4 h-4 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728L5.636 5.636m12.728 12.728L18.364 5.636M5.636 18.364L18.364 5.636" />
                            </svg>
                            <svg v-else class="w-4 h-4 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                            {{ department.isActive ? 'Deactivate' : 'Activate' }}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Grid View -->
        <div v-else class="p-6">
          <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
            <div v-for="department in departments" :key="department.id" class="group bg-white rounded-xl border border-gray-200 hover:border-blue-300 hover:shadow-lg transition-all duration-300 overflow-hidden">
              <div class="p-6">
                <div class="flex items-center justify-between mb-4">
                  <div class="flex items-center">
                    <div class="w-12 h-12 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg group-hover:scale-105 transition-transform duration-200">
                      <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                      </svg>
                    </div>
                    <span
                      :class="[
                        'ml-3 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium',
                        department.isActive
                          ? 'bg-green-100 text-green-800'
                          : 'bg-red-100 text-red-800'
                      ]"
                    >
                      {{ department.isActive ? 'Active' : 'Inactive' }}
                    </span>
                  </div>
                </div>
                
                <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ department.name }}</h3>
                <p class="text-sm text-gray-600 mb-4" v-if="department.description">
                  {{ department.description }}
                </p>
                
                <div class="space-y-3 mb-6">
                  <div class="flex items-center justify-between">
                    <span class="text-sm text-gray-500">Manager:</span>
                    <span class="text-sm font-medium text-gray-900">{{ getManagerName(department.managerId) }}</span>
                  </div>
                  <div class="flex items-center justify-between">
                    <span class="text-sm text-gray-500">Users:</span>
                    <span class="text-sm font-semibold text-blue-600">{{ department.userCount || 0 }}</span>
                  </div>
                  <div class="flex items-center justify-between">
                    <span class="text-sm text-gray-500">Projects:</span>
                    <span class="text-sm font-semibold text-purple-600">{{ department.projectCount || 0 }}</span>
                  </div>
                </div>
                
                <div class="flex items-center justify-between pt-4 border-t border-gray-100">
                  <button
                    @click="viewDepartment(department)"
                    class="text-blue-600 hover:text-blue-800 font-medium text-sm transition-colors"
                  >
                    View Details
                  </button>
                  <div class="flex items-center space-x-2">
                    <button
                      @click="editDepartment(department)"
                      class="p-2 text-gray-400 hover:text-blue-600 transition-colors rounded-lg hover:bg-blue-50"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button
                      @click="manageDepartmentUsers(department)"
                      class="p-2 text-gray-400 hover:text-green-600 transition-colors rounded-lg hover:bg-green-50"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Enhanced Empty State -->
        <div v-if="departments.length === 0 && !loading" class="text-center py-16">
          <div class="max-w-md mx-auto">
            <div class="w-24 h-24 mx-auto bg-gradient-to-r from-blue-100 to-indigo-100 rounded-full flex items-center justify-center mb-6">
              <svg class="w-12 h-12 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-2">No departments found</h3>
            <p class="text-gray-600 mb-8">
              Get started by creating your first department to organize your team structure.
            </p>
            <button
              @click="showCreateModal = true"
              class="inline-flex items-center px-6 py-3 border border-transparent shadow-lg text-base font-medium rounded-lg text-white bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transform hover:scale-105 transition-all duration-200"
            >
              <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
              </svg>
              Create Your First Department
            </button>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="p-8">
          <div class="animate-pulse space-y-4">
            <div v-for="i in 5" :key="i" class="flex items-center space-x-4">
              <div class="rounded-full bg-gray-200 h-12 w-12"></div>
              <div class="flex-1 space-y-2">
                <div class="h-4 bg-gray-200 rounded w-3/4"></div>
                <div class="h-4 bg-gray-200 rounded w-1/2"></div>
              </div>
              <div class="space-y-2">
                <div class="h-4 bg-gray-200 rounded w-16"></div>
                <div class="h-4 bg-gray-200 rounded w-12"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Enhanced Create/Edit Department Modal -->
      <div v-if="showCreateModal || showEditModal" class="fixed inset-0 z-50 overflow-y-auto">
        <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
          <!-- Background overlay -->
          <div class="fixed inset-0 transition-opacity" aria-hidden="true">
            <div class="absolute inset-0 bg-gray-900 opacity-75" @click="closeModal"></div>
          </div>

          <!-- Modal panel -->
          <div class="inline-block align-bottom bg-white rounded-2xl text-left overflow-hidden shadow-2xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl sm:w-full">
            <!-- Modal Header -->
            <div class="bg-gradient-to-r from-blue-500 to-indigo-600 px-6 py-4">
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <div class="w-8 h-8 bg-white bg-opacity-20 rounded-lg flex items-center justify-center mr-3">
                    <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                    </svg>
                  </div>
                  <h3 class="text-lg font-semibold text-white">
                    {{ showCreateModal ? 'Create New Department' : 'Edit Department' }}
                  </h3>
                </div>
                <button
                  @click="closeModal"
                  class="text-white hover:text-gray-200 transition-colors"
                >
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            </div>

            <!-- Modal Body -->
            <form @submit.prevent="saveDepartment">
              <div class="bg-white px-6 py-6">
                <div class="space-y-6">
                  <!-- Department Name -->
                  <div>
                    <label for="department-name" class="block text-sm font-semibold text-gray-700 mb-2">
                      Department Name <span class="text-red-500">*</span>
                    </label>
                    <input
                      id="department-name"
                      v-model="formData.name"
                      type="text"
                      required
                      :class="[
                        'block w-full px-4 py-3 border rounded-lg shadow-sm transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500',
                        formErrors.name ? 'border-red-300 bg-red-50' : 'border-gray-300 focus:border-blue-500'
                      ]"
                      placeholder="Enter department name"
                    />
                    <p v-if="formErrors.name" class="mt-1 text-sm text-red-600">{{ formErrors.name }}</p>
                  </div>

                  <!-- Description -->
                  <div>
                    <label for="department-description" class="block text-sm font-semibold text-gray-700 mb-2">
                      Description
                    </label>
                    <textarea
                      id="department-description"
                      v-model="formData.description"
                      rows="3"
                      class="block w-full px-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 resize-none"
                      placeholder="Enter department description (optional)"
                    ></textarea>
                  </div>

                  <!-- Manager and Parent Department Row -->
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Manager -->
                    <div>
                      <label for="department-manager" class="block text-sm font-semibold text-gray-700 mb-2">
                        Manager
                      </label>
                      <select
                        id="department-manager"
                        v-model="formData.managerId"
                        class="block w-full px-4 py-3 border border-gray-300 bg-white rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
                      >
                        <option value="">Select a manager</option>
                        <option v-for="manager in managers" :key="manager.id" :value="manager.id">
                          {{ manager.fullName }}
                        </option>
                      </select>
                    </div>

                    <!-- Parent Department -->
                    <div>
                      <label for="department-parent" class="block text-sm font-semibold text-gray-700 mb-2">
                        Parent Department
                      </label>
                      <select
                        id="department-parent"
                        v-model="formData.parentId"
                        class="block w-full px-4 py-3 border border-gray-300 bg-white rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
                      >
                        <option value="">No parent (Root department)</option>
                        <option v-for="dept in rootDepartments" :key="dept.id" :value="dept.id">
                          {{ dept.name }}
                        </option>
                      </select>
                    </div>
                  </div>

                  <!-- Active Status -->
                  <div>
                    <div class="flex items-center">
                      <input
                        id="department-active"
                        v-model="formData.isActive"
                        type="checkbox"
                        class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                      />
                      <label for="department-active" class="ml-3 block text-sm font-medium text-gray-900">
                        Active Department
                      </label>
                    </div>
                    <p class="mt-1 text-sm text-gray-500">
                      Inactive departments will be hidden from users and cannot be assigned to projects.
                    </p>
                  </div>
                </div>
              </div>

              <!-- Modal Footer -->
              <div class="bg-gray-50 px-6 py-4 flex flex-col sm:flex-row sm:items-center sm:justify-end sm:space-x-3 space-y-3 sm:space-y-0">
                <button
                  type="button"
                  @click="closeModal"
                  class="w-full sm:w-auto inline-flex justify-center items-center px-6 py-3 border border-gray-300 shadow-sm text-sm font-medium rounded-lg text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  :disabled="loading || !isFormValid"
                  class="w-full sm:w-auto inline-flex justify-center items-center px-6 py-3 border border-transparent shadow-sm text-sm font-medium rounded-lg text-white bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transform hover:scale-105 transition-all duration-200"
                >
                  <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  {{ loading ? 'Saving...' : (showCreateModal ? 'Create Department' : 'Update Department') }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notification'
import { useRouter } from 'vue-router'
import { adminApi, type Department, type DashboardStats } from '@/services/adminApi'
import type { User } from '@/services/api'

const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const router = useRouter()

// Reactive data
const departments = ref<Department[]>([])
const managers = ref<User[]>([])
const rootDepartments = ref<Department[]>([])
const loading = ref(false)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const currentDepartment = ref<Department | null>(null)
const viewMode = ref<'table' | 'grid'>('table')
const activeDropdown = ref<number | null>(null)

// Stats
const stats = ref<DashboardStats>({
  totalUsers: 0,
  totalDepartments: 0,
  activeProjects: 0,
  totalFiles: 0
})

// Additional stats for departments
const departmentStats = ref({
  activeDepartments: 0
})

// Filters
const filters = ref({
  name: '',
  managerId: '',
  isActive: ''
})

// Form data
const formData = ref({
  name: '',
  description: '',
  managerId: '',
  parentId: '',
  isActive: true
})

// Form validation
const formErrors = ref({
  name: ''
})

// Click away handler for dropdown
const handleClickAway = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (!target.closest('.dropdown-container')) {
    activeDropdown.value = null
  }
}

// Computed
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

const isFormValid = computed(() => {
  return formData.value.name.trim().length > 0 && !formErrors.value.name
})

// Methods
const refreshData = async () => {
  await Promise.all([
    loadDepartments(),
    loadManagers()
  ])
  await loadRootDepartments()
}

const clearFilters = () => {
  filters.value = {
    name: '',
    managerId: '',
    isActive: ''
  }
  applyFilters()
}

const toggleDropdown = (departmentId: number) => {
  activeDropdown.value = activeDropdown.value === departmentId ? null : departmentId
}

const getParentDepartmentName = (parentId?: number): string => {
  if (!parentId) return ''
  const parent = departments.value.find(d => d.id === parentId)
  return parent ? parent.name : 'Unknown'
}

const validateForm = () => {
  formErrors.value.name = ''
  
  if (!formData.value.name.trim()) {
    formErrors.value.name = 'Department name is required'
    return false
  }
  
  if (formData.value.name.trim().length < 2) {
    formErrors.value.name = 'Department name must be at least 2 characters'
    return false
  }
  
  return true
}

const loadDepartments = async () => {
  try {
    loading.value = true
    const response = await adminApi.getDepartmentsWithStats()
    if (response.success && response.data) {
      departments.value = response.data
      stats.value.totalDepartments = response.data.length
      departmentStats.value.activeDepartments = response.data.filter(d => d.isActive).length
    } else {
      notificationStore.error('Failed to Load Departments', 'Unable to fetch department data')
    }
  } catch (error) {
    console.error('Failed to load departments:', error)
    notificationStore.error('Connection Error', 'Failed to connect to the server')
  } finally {
    loading.value = false
  }
}

const loadManagers = async () => {
  try {
    const response = await adminApi.getUsers({ role: 'ADMIN' })
    if (response.success && response.data) {
      managers.value = response.data.content
    }
  } catch (error) {
    console.error('Failed to load managers:', error)
    // Fallback to empty array
    managers.value = []
  }
}

const loadRootDepartments = async () => {
  try {
    // Load departments that can be parents (no parentId)
    rootDepartments.value = departments.value.filter(d => !d.parentId)
  } catch (error) {
    console.error('Failed to load root departments:', error)
  }
}

const getManagerName = (managerId?: number): string => {
  if (!managerId) return 'No manager assigned'
  const manager = managers.value.find(m => m.id === managerId)
  return manager ? manager.fullName : 'Unknown manager'
}

const applyFilters = async () => {
  // TODO: Implement filtering logic
  await loadDepartments()
}

const viewDepartment = (department: Department) => {
  // TODO: Navigate to department detail view
  router.push(`/admin/departments/${department.id}`)
}

const editDepartment = (department: Department) => {
  currentDepartment.value = department
  formData.value = {
    name: department.name,
    description: department.description || '',
    managerId: department.managerId?.toString() || '',
    parentId: department.parentId?.toString() || '',
    isActive: department.isActive
  }
  showEditModal.value = true
}

const manageDepartmentUsers = (department: Department) => {
  // TODO: Navigate to department user management
  router.push(`/admin/departments/${department.id}/users`)
}

const toggleDepartmentStatus = async (department: Department) => {
  try {
    loading.value = true
    const response = await adminApi.updateDepartment(department.id, { isActive: !department.isActive })
    if (response.success) {
      await loadDepartments()
      const statusText = !department.isActive ? 'activated' : 'deactivated'
      notificationStore.success('Department Status Updated', `${department.name} has been ${statusText}`)
    } else {
      notificationStore.error('Failed to Update Status', 'Please try again')
    }
  } catch (error) {
    console.error('Failed to toggle department status:', error)
    notificationStore.error('Operation Failed', 'An unexpected error occurred')
  } finally {
    loading.value = false
  }
}

const saveDepartment = async () => {
  if (!validateForm()) {
    return
  }

  try {
    loading.value = true
    
    const departmentData = {
      name: formData.value.name.trim(),
      description: formData.value.description.trim(),
      managerId: formData.value.managerId ? parseInt(formData.value.managerId) : undefined,
      parentId: formData.value.parentId ? parseInt(formData.value.parentId) : undefined,
      isActive: formData.value.isActive
    }
    
    if (showCreateModal.value) {
      const response = await adminApi.createDepartment(departmentData)
      if (response.success) {
        notificationStore.success('Department Created', `${formData.value.name} has been created successfully`)
      } else {
        notificationStore.error('Failed to Create Department', 'Please try again')
        return
      }
    } else if (showEditModal.value && currentDepartment.value) {
      const response = await adminApi.updateDepartment(currentDepartment.value.id, departmentData)
      if (response.success) {
        notificationStore.success('Department Updated', `${formData.value.name} has been updated successfully`)
      } else {
        notificationStore.error('Failed to Update Department', 'Please try again')
        return
      }
    }
    
    closeModal()
    await refreshData()
  } catch (error) {
    console.error('Failed to save department:', error)
    notificationStore.error('Operation Failed', 'An unexpected error occurred')
  } finally {
    loading.value = false
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  currentDepartment.value = null
  formData.value = {
    name: '',
    description: '',
    managerId: '',
    parentId: '',
    isActive: true
  }
  formErrors.value = {
    name: ''
  }
}

// Lifecycle
onMounted(async () => {
  // Check admin access
  if (!isAdmin.value) {
    console.warn('Access denied: Admin role required')
    router.push('/')
    return
  }

  // Add click away event listener
  document.addEventListener('click', handleClickAway)

  // Load initial data
  await refreshData()
  
  // Load dashboard stats
  try {
    const statsResponse = await adminApi.getDashboardStats()
    if (statsResponse.success && statsResponse.data) {
      stats.value = statsResponse.data
    }
  } catch (error) {
    console.error('Failed to load dashboard stats:', error)
  }
})

onUnmounted(() => {
  // Remove click away event listener
  document.removeEventListener('click', handleClickAway)
})
</script>
