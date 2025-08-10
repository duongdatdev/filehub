<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header (compact) -->
      <div class="mb-3 text-center relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-to-r from-blue-50 via-purple-50 to-indigo-50 rounded-xl opacity-40"></div>
        <div class="relative z-10 py-3">
          <div class="inline-flex items-center justify-center w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg mb-2 shadow-md">
            <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.367 2.684 3 3 0 00-5.367-2.684z"/>
            </svg>
          </div>
          <h1 class="text-xl font-extrabold bg-gradient-to-r from-gray-900 via-blue-800 to-purple-800 bg-clip-text text-transparent mb-1">
            Shared Files
          </h1>
          <p class="text-sm text-gray-600 max-w-md mx-auto leading-normal">
            Discover and access files shared across your departments and projects. <span class="text-blue-600 font-medium">Collaborate efficiently</span> with your team members.
          </p>
          <!-- Quick stats (tiny, horizontal) -->
          <div v-if="totalElements > 0" class="mt-0.5 flex justify-center items-center gap-1 text-[10px] text-gray-400 font-medium">
            <div class="flex items-center gap-0.5 px-1 py-0.5 rounded bg-blue-50">
              <svg class="w-2 h-2 mr-0.5 text-blue-500" fill="currentColor" viewBox="0 0 8 8"><circle cx="4" cy="4" r="4"/></svg>
              <span>{{ totalElements }}</span>
              <span class="ml-0.5">files</span>
            </div>
            <div class="flex items-center gap-0.5 px-1 py-0.5 rounded bg-green-50">
              <svg class="w-2 h-2 mr-0.5 text-green-500" fill="currentColor" viewBox="0 0 8 8"><circle cx="4" cy="4" r="4"/></svg>
              <span>{{ userDepartments.length }}</span>
              <span class="ml-0.5">depts</span>
            </div>
            <div class="flex items-center gap-0.5 px-1 py-0.5 rounded bg-purple-50">
              <svg class="w-2 h-2 mr-0.5 text-purple-500" fill="currentColor" viewBox="0 0 8 8"><circle cx="4" cy="4" r="4"/></svg>
              <span>{{ userProjects.length }}</span>
              <span class="ml-0.5">projects</span>
            </div>
          </div>
        </div>
      </div>

      <!-- View Selector -->
      <div class="bg-white rounded-xl shadow-md border border-gray-100 p-3 mb-4 backdrop-blur-sm bg-white/90">
        <div class="flex flex-col lg:flex-row gap-3 items-start lg:items-center min-h-[67px]">
          <div class="flex flex-col sm:flex-row gap-4 sm:gap-0">
            <label class="text-base font-semibold text-gray-800 mb-1 sm:mb-0 sm:mr-4 flex items-center">
              <svg class="w-5 h-5 mr-2 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
              </svg>
              View Files By:
            </label>
            <div class="flex rounded-lg shadow-sm bg-gray-50 p-0.5">
              <button @click="currentView = 'all'" :class="[
                'px-3 py-2 text-xs font-semibold rounded border-0 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200 flex items-center space-x-1',
                currentView === 'all' ? 'bg-white text-blue-700 shadow-md scale-105' : 'text-gray-600 hover:text-gray-800 hover:bg-white/50'
              ]">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
                </svg>
                <span>All Files</span>
              </button>
              <button @click="currentView = 'department'" :class="[
                'px-3 py-2 text-xs font-semibold rounded border-0 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200 flex items-center space-x-1',
                currentView === 'department' ? 'bg-white text-blue-700 shadow-md scale-105' : 'text-gray-600 hover:text-gray-800 hover:bg-white/50'
              ]">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                </svg>
                <span>Department</span>
              </button>
              <button @click="currentView = 'project'" :class="[
                'px-3 py-2 text-xs font-semibold rounded border-0 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200 flex items-center space-x-1',
                currentView === 'project' ? 'bg-white text-blue-700 shadow-md scale-105' : 'text-gray-600 hover:text-gray-800 hover:bg-white/50'
              ]">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                </svg>
                <span>Project</span>
              </button>
            </div>
          </div>

          <!-- Department/Project Selector -->
          <div v-if="currentView === 'department'" class="flex items-center space-x-2 bg-gradient-to-r from-blue-50 to-indigo-50 p-2 rounded-lg border border-blue-200 shadow-sm hover:shadow-md transition-shadow duration-200 min-h-[56px]">
            <div class="w-7 h-7 bg-blue-100 rounded flex items-center justify-center">
              <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
              </svg>
            </div>
            <div class="flex-1">
              <label class="block text-xs font-semibold text-blue-900 mb-0.5">Department</label>
              <select v-model="selectedDepartmentId" @change="onDepartmentChange"
                class="w-full min-w-[120px] rounded border-0 bg-white shadow-sm ring-1 ring-blue-200 focus:ring-2 focus:ring-blue-500 text-gray-900 font-medium py-1.5 px-2 transition-all duration-200 text-xs">
                <option value="">🏢 Select Department</option>
                <option v-for="dept in userDepartments" :key="dept.id" :value="dept.id">
                  {{ dept.name }}
                </option>
              </select>
            </div>
          </div>

          <div v-if="currentView === 'project'" class="flex items-center space-x-2 bg-gradient-to-r from-purple-50 to-pink-50 p-2 rounded-lg border border-purple-200 shadow-sm hover:shadow-md transition-shadow duration-200 min-h-[56px]">
            <div class="w-7 h-7 bg-purple-100 rounded flex items-center justify-center">
              <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
              </svg>
            </div>
            <div class="flex-1">
              <label class="block text-xs font-semibold text-purple-900 mb-0.5">Project</label>
              <select v-model="selectedProjectId" @change="onProjectChange"
                class="w-full min-w-[120px] rounded border-0 bg-white shadow-sm ring-1 ring-purple-200 focus:ring-2 focus:ring-purple-500 text-gray-900 font-medium py-1.5 px-2 transition-all duration-200 text-xs">
                <option value="">📋 Select Project</option>
                <option v-for="project in userProjects" :key="project.id" :value="project.id">
                  {{ project.name }}
                </option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <!-- Filters -->
      <div class="bg-white rounded-xl shadow-md border border-gray-100 p-3 mb-4 backdrop-blur-sm bg-white/90">
        <div class="flex items-center justify-between mb-3">
          <h3 class="text-base font-bold text-gray-900 flex items-center">
            <div class="w-6 h-6 bg-gradient-to-r from-blue-500 to-purple-600 rounded flex items-center justify-center mr-2">
              <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.414A1 1 0 013 6.707V4z"/>
              </svg>
            </div>
            Filter & Search
          </h3>
          <button @click="clearFilters" 
            class="text-sm text-gray-600 hover:text-red-600 flex items-center space-x-2 px-4 py-2 rounded-lg hover:bg-red-50 transition-all duration-200 font-medium border border-gray-200 hover:border-red-200">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
            </svg>
            <span>Clear All</span>
          </button>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-2">
          <!-- Search by filename -->
          <div class="space-y-1.5">
            <label class="flex items-center text-xs font-semibold text-gray-700">
              <svg class="w-4 h-4 mr-2 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
              </svg>
              Search Files
            </label>
            <div class="relative group">
              <input v-model="filters.filename" type="text" placeholder="Type to search files..."
              class="w-full pl-8 pr-2 py-2 rounded border-0 bg-gray-50 shadow-sm ring-1 ring-gray-200 focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all duration-200 placeholder-gray-400 text-xs"
                @input="debouncedSearch" />
              <svg class="w-5 h-5 text-gray-400 absolute left-3 top-3.5 group-focus-within:text-blue-500 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
              </svg>
              <div v-if="filters.filename" class="absolute right-3 top-3.5">
                <button @click="filters.filename = ''; applyFilters()" class="text-gray-400 hover:text-gray-600 transition-colors">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <!-- File Type Filter -->
          <div class="space-y-1.5">
            <label class="flex items-center text-xs font-semibold text-gray-700">
              <svg class="w-4 h-4 mr-2 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
              </svg>
              File Type
            </label>
            <select v-model="filters.fileTypeId" @change="applyFilters"
              class="w-full rounded border-0 bg-gray-50 shadow-sm ring-1 ring-gray-200 focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all duration-200 py-2 px-2 text-gray-900 font-medium text-xs">
              <option value="">📄 All Types</option>
              <option v-for="type in fileTypes" :key="type.id" :value="type.id">
                {{ type.name }}
              </option>
            </select>
          </div>

          <!-- Department Category Filter -->
          <div class="space-y-1.5">
            <label class="flex items-center text-xs font-semibold text-gray-700">
              <svg class="w-4 h-4 mr-2 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"/>
              </svg>
              Category
            </label>
            <select v-model="filters.departmentCategoryId" @change="applyFilters"
              class="w-full rounded border-0 bg-gray-50 shadow-sm ring-1 ring-gray-200 focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all duration-200 py-2 px-2 text-gray-900 font-medium text-xs">
              <option value="">🏷️ All Categories</option>
              <option v-for="category in availableDepartmentCategories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </div>

          <!-- Sort -->
          <div class="space-y-1.5">
            <label class="flex items-center text-xs font-semibold text-gray-700">
              <svg class="w-4 h-4 mr-2 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4h13M3 8h9m-9 4h9m5-4v12m0 0l-4-4m4 4l4-4"/>
              </svg>
              Sort By
            </label>
            <select v-model="sortBy" @change="applyFilters"
              class="w-full rounded border-0 bg-gray-50 shadow-sm ring-1 ring-gray-200 focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all duration-200 py-2 px-2 text-gray-900 font-medium text-xs">
              <option value="uploadedAt">📅 Upload Date</option>
              <option value="originalFilename">📝 Filename</option>
              <option value="fileSize">📊 File Size</option>
              <option value="downloadCount">⬇️ Downloads</option>
            </select>
          </div>
        </div>

        <div class="mt-3 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3">
          <div class="flex flex-col sm:flex-row items-start sm:items-center space-y-3 sm:space-y-0 sm:space-x-6">
            <div class="flex items-center space-x-2">
              <label class="text-xs text-gray-700 font-semibold">Sort Order:</label>
              <button @click="toggleSortDirection"
                class="flex items-center space-x-1 text-xs text-blue-600 hover:text-blue-800 px-2 py-1 rounded hover:bg-blue-50 transition-all duration-200 font-medium border border-blue-200 hover:border-blue-300">
                <span>{{ sortDirection === 'DESC' ? '📅 Newest First' : '📅 Oldest First' }}</span>
                <svg class="w-4 h-4 transition-transform duration-200" :class="{ 'rotate-180': sortDirection === 'ASC' }" fill="none"
                  stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
            </div>
          </div>

          <div class="flex items-center space-x-4">
            <!-- View Layout Toggle -->
            <div class="flex rounded-lg shadow-sm bg-gray-50 p-0.5">
              <button @click="viewLayout = 'grid'" :class="[
                'px-3 py-1.5 text-xs font-semibold rounded border-0 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200 flex items-center space-x-1',
                viewLayout === 'grid' ? 'bg-white text-blue-700 shadow-md scale-105' : 'text-gray-600 hover:text-gray-800 hover:bg-white/50'
              ]">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"/>
                </svg>
                <span>Grid</span>
              </button>
              <button @click="viewLayout = 'list'" :class="[
                'px-3 py-1.5 text-xs font-semibold rounded border-0 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200 flex items-center space-x-1',
                viewLayout === 'list' ? 'bg-white text-blue-700 shadow-md scale-105' : 'text-gray-600 hover:text-gray-800 hover:bg-white/50'
              ]">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 10h16M4 14h16M4 18h16"/>
                </svg>
                <span>List</span>
              </button>
              <button @click="viewLayout = 'table'" :class="[
                'px-3 py-1.5 text-xs font-semibold rounded border-0 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200 flex items-center space-x-1',
                viewLayout === 'table' ? 'bg-white text-blue-700 shadow-md scale-105' : 'text-gray-600 hover:text-gray-800 hover:bg-white/50'
              ]">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M3 14h18m-9-4v8m-7 0V4a1 1 0 011-1h3M3 10V9a2 2 0 012-2h14a2 2 0 012 2v1M3 10v11a2 2 0 002 2h14a2 2 0 002-2V10"/>
                </svg>
                <span>Table</span>
              </button>
            </div>
            
            <div class="text-xs text-gray-600 bg-gradient-to-r from-gray-50 to-blue-50 px-2 py-1 rounded border border-gray-200">
              <span class="font-bold text-blue-600">{{ files.length }}</span> 
              <span class="text-gray-500">of</span> 
              <span class="font-bold text-blue-600">{{ totalElements }}</span> 
              <span class="text-gray-700">files</span>
            </div>
            <div v-if="totalElements > 0" class="text-xs text-gray-500 bg-gray-50 px-2 py-1 rounded">
              Page {{ currentPage + 1 }} of {{ totalPages }}
            </div>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-20">
        <div class="inline-block relative">
          <div class="animate-spin rounded-full h-16 w-16 border-4 border-blue-200 border-t-blue-600 shadow-lg"></div>
          <div class="absolute inset-0 flex items-center justify-center">
            <svg class="w-8 h-8 text-blue-600 animate-pulse" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
            </svg>
          </div>
        </div>
        <h3 class="mt-6 text-xl font-bold text-gray-900">Loading Files...</h3>
        <p class="mt-3 text-gray-600 max-w-md mx-auto">Please wait while we fetch your files. This won't take long!</p>
        <div class="mt-4 flex justify-center space-x-1">
          <div class="w-2 h-2 bg-blue-500 rounded-full animate-bounce"></div>
          <div class="w-2 h-2 bg-blue-500 rounded-full animate-bounce" style="animation-delay: 0.1s"></div>
          <div class="w-2 h-2 bg-blue-500 rounded-full animate-bounce" style="animation-delay: 0.2s"></div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="files.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-lg border border-gray-100">
        <div class="mx-auto h-32 w-32 text-gray-300 mb-6 relative">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" class="w-full h-full">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1"
              d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <div class="absolute -top-2 -right-2 w-8 h-8 bg-yellow-100 rounded-full flex items-center justify-center">
            <svg class="w-4 h-4 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z"/>
            </svg>
          </div>
        </div>
        <h3 class="text-2xl font-bold text-gray-900 mb-3">No Files Found</h3>
        <p class="text-gray-600 mb-6 max-w-lg mx-auto text-lg leading-relaxed">
          {{ getNoFilesMessage() }}
        </p>
        <button v-if="hasActiveFilters" @click="clearFilters" 
          class="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-xl text-white bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transform hover:scale-105 transition-all duration-200 shadow-lg">
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
          </svg>
          Clear Filters & View All
        </button>
      </div>

      <!-- Files Grid View -->
      <div v-if="viewLayout === 'grid'" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3">
        <div v-for="file in files" :key="file.id"
          class="group bg-white rounded-lg shadow border border-gray-100 hover:shadow-lg hover:border-gray-200 transition-all duration-200 overflow-hidden transform hover:-translate-y-0.5">
          <!-- File Header -->
          <div class="p-3 border-b border-gray-50">
            <div class="flex items-start justify-between">
              <div class="flex-1 min-w-0">
                <h3 class="text-xs font-bold text-gray-900 truncate leading-tight mb-1">
                  {{ file.title || file.originalFilename }}
                </h3>
                <div class="flex items-center mt-2 text-xs text-gray-500 space-x-2">
                  <div class="flex items-center space-x-1.5">
                    <div class="w-6 h-6 bg-blue-100 rounded-full flex items-center justify-center">
                      <svg class="w-3 h-3 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                      </svg>
                    </div>
                    <span class="font-medium">{{ file.uploaderName }}</span>
                  </div>
                  <div class="flex items-center space-x-1.5">
                    <div class="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center">
                      <svg class="w-3 h-3 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                      </svg>
                    </div>
                    <span class="font-medium">{{ formatDate(file.uploadedAt) }}</span>
                  </div>
                </div>
              </div>
              <div class="flex flex-col items-end space-y-1 ml-2">
                <!-- Visibility Badge -->
                <span :class="getVisibilityBadgeClass(file.visibility)"
                  class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold shadow-sm">
                  {{ file.visibility }}
                </span>
                <!-- Owner indicator -->
                <div v-if="file.uploaderId === currentUserId" 
                  class="flex items-center text-xs text-emerald-700 font-bold bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">
                  <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                  </svg>
                  Your File
                </div>
              </div>
            </div>
          </div>

          <!-- File Content -->
          <div class="p-3">
            <!-- File Info Grid -->
            <div class="grid grid-cols-2 gap-1 text-xs mb-2">
              <div class="bg-gradient-to-br from-gray-50 to-gray-100 p-2 rounded border border-gray-200">
                <div class="flex items-center justify-between">
                  <span class="text-gray-600 font-medium">Size</span>
                  <span class="font-bold text-gray-900">{{ formatFileSize(file.fileSize) }}</span>
                </div>
              </div>
              <div class="bg-gradient-to-br from-blue-50 to-blue-100 p-2 rounded border border-blue-200">
                <div class="flex items-center justify-between">
                  <span class="text-blue-700 font-medium">Type</span>
                  <span class="font-bold text-blue-900">{{ file.fileTypeName || getFileTypeFromContent(file.contentType) }}</span>
                </div>
              </div>
              <div class="bg-gradient-to-br from-green-50 to-green-100 p-2 rounded border border-green-200">
                <div class="flex items-center justify-between">
                  <span class="text-green-700 font-medium">Downloads</span>
                  <span class="font-bold text-green-900 flex items-center">
                    <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-4-4m4 4l4-4"/>
                    </svg>
                    {{ file.downloadCount }}
                  </span>
                </div>
              </div>
              <div class="bg-gradient-to-br from-purple-50 to-purple-100 p-2 rounded border border-purple-200">
                <div class="flex items-center justify-between">
                  <span class="text-purple-700 font-medium">Access</span>
                  <span class="text-xs font-bold" :class="getVisibilityTextColor(file.visibility)">
                    {{ file.visibility }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Department/Project Info -->
            <div v-if="file.departmentName || file.projectName" class="space-y-1 mb-2">
              <div v-if="file.departmentName" class="flex items-center text-xs text-blue-800 bg-gradient-to-r from-blue-50 to-blue-100 px-2 py-1 rounded border border-blue-200">
                <div class="w-8 h-8 bg-blue-200 rounded-lg flex items-center justify-center mr-3">
                  <svg class="w-4 h-4 text-blue-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5"/>
                  </svg>
                </div>
                <span class="font-bold">{{ file.departmentName }}</span>
              </div>
              <div v-if="file.projectName" class="flex items-center text-xs text-purple-800 bg-gradient-to-r from-purple-50 to-purple-100 px-2 py-1 rounded border border-purple-200">
                <div class="w-8 h-8 bg-purple-200 rounded-lg flex items-center justify-center mr-3">
                  <svg class="w-4 h-4 text-purple-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2"/>
                  </svg>
                </div>
                <span class="font-bold">{{ file.projectName }}</span>
              </div>
            </div>

            <!-- Description -->
            <div v-if="file.description" class="mb-2">
              <p class="text-xs text-gray-700 bg-gradient-to-r from-gray-50 to-gray-100 p-2 rounded line-clamp-2 border border-gray-200 leading-snug">{{ file.description }}</p>
            </div>

            <!-- Tags -->
            <div v-if="file.tags && file.tags.length > 0" class="mb-1">
              <div class="flex flex-wrap gap-1">
                <span v-for="tag in file.tags.slice(0, 3)" :key="tag"
                  class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold bg-gradient-to-r from-blue-100 to-blue-200 text-blue-800 border border-blue-300">
                  <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"/>
                  </svg>
                  {{ tag }}
                </span>
                <span v-if="file.tags.length > 3"
                  class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold bg-gradient-to-r from-gray-100 to-gray-200 text-gray-800 border border-gray-300">
                  +{{ file.tags.length - 3 }} more
                </span>
              </div>
            </div>
          </div>

          <!-- File Actions -->
          <div class="px-3 py-2 bg-gradient-to-r from-gray-50 via-blue-50 to-purple-50 border-t border-gray-100">
            <div class="flex justify-center space-x-2">
              <button @click="downloadFile(file)"
                class="flex-1 inline-flex items-center justify-center px-2 py-2 border-0 text-xs font-bold rounded text-white bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200 group shadow-md hover:shadow-lg transform hover:scale-105">
                <svg class="w-3 h-3 mr-1 group-hover:animate-bounce" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M12 10v6m0 0l-4-4m4 4l4-4m5 10v1a2 2 0 01-2 2H5a2 2 0 01-2-2v-1" />
                </svg>
                Download
              </button>
              <button @click="viewFile(file)"
                class="flex-1 inline-flex items-center justify-center px-2 py-2 border border-gray-300 text-xs font-bold rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200 shadow-md hover:shadow-lg transform hover:scale-105">
                <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                Preview
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Files List View -->
      <div v-else-if="viewLayout === 'list'" class="space-y-2">
        <div v-for="file in files" :key="file.id" 
          class="bg-white rounded-lg shadow border border-gray-100 hover:shadow-md hover:border-gray-200 transition-all duration-200 p-4 group">
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-4 flex-1 min-w-0">
              <!-- File Icon & Basic Info -->
              <div class="flex-shrink-0 w-12 h-12 bg-gradient-to-r from-blue-100 to-purple-100 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                </svg>
              </div>
              
              <!-- File Details -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center space-x-2 mb-1">
                  <h3 class="text-sm font-bold text-gray-900 truncate">
                    {{ file.title || file.originalFilename }}
                  </h3>
                  <span :class="getVisibilityBadgeClass(file.visibility)"
                    class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold shadow-sm flex-shrink-0">
                    {{ file.visibility }}
                  </span>
                  <div v-if="file.uploaderId === currentUserId" 
                    class="flex items-center text-xs text-emerald-700 font-bold bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200 flex-shrink-0">
                    <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                    </svg>
                    Your File
                  </div>
                </div>
                
                <div class="flex items-center space-x-4 text-xs text-gray-500">
                  <div class="flex items-center space-x-1">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                    </svg>
                    <span class="font-medium">{{ file.uploaderName }}</span>
                  </div>
                  <div class="flex items-center space-x-1">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                    <span>{{ formatDate(file.uploadedAt) }}</span>
                  </div>
                  <div class="flex items-center space-x-1">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                    </svg>
                    <span class="font-medium">{{ file.fileTypeName || getFileTypeFromContent(file.contentType) }}</span>
                  </div>
                  <div class="flex items-center space-x-1">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4"/>
                    </svg>
                    <span class="font-medium">{{ formatFileSize(file.fileSize) }}</span>
                  </div>
                  <div class="flex items-center space-x-1">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-4-4m4 4l4-4"/>
                    </svg>
                    <span class="font-medium">{{ file.downloadCount }} downloads</span>
                  </div>
                </div>
                
                <!-- Department/Project Info -->
                <div v-if="file.departmentName || file.projectName" class="flex items-center space-x-2 mt-1">
                  <div v-if="file.departmentName" class="flex items-center text-xs text-blue-700 bg-blue-50 px-2 py-1 rounded border border-blue-200">
                    <svg class="w-3 h-3 mr-1 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5"/>
                    </svg>
                    <span class="font-medium">{{ file.departmentName }}</span>
                  </div>
                  <div v-if="file.projectName" class="flex items-center text-xs text-purple-700 bg-purple-50 px-2 py-1 rounded border border-purple-200">
                    <svg class="w-3 h-3 mr-1 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2"/>
                    </svg>
                    <span class="font-medium">{{ file.projectName }}</span>
                  </div>
                </div>
                
                <!-- Description -->
                <div v-if="file.description" class="mt-1">
                  <p class="text-xs text-gray-600 line-clamp-1">{{ file.description }}</p>
                </div>
                
                <!-- Tags -->
                <div v-if="file.tags && file.tags.length > 0" class="flex flex-wrap gap-1 mt-1">
                  <span v-for="tag in file.tags.slice(0, 4)" :key="tag"
                    class="inline-flex items-center px-1.5 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-800">
                    {{ tag }}
                  </span>
                  <span v-if="file.tags.length > 4"
                    class="inline-flex items-center px-1.5 py-0.5 rounded text-xs font-medium bg-gray-100 text-gray-600">
                    +{{ file.tags.length - 4 }}
                  </span>
                </div>
              </div>
            </div>
            
            <!-- Actions -->
            <div class="flex items-center space-x-2 flex-shrink-0 ml-4">
              <button @click="downloadFile(file)"
                class="inline-flex items-center px-3 py-2 border-0 text-sm font-medium rounded text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200 shadow-sm hover:shadow-md">
                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-4-4m4 4l4-4m5 10v1a2 2 0 01-2 2H5a2 2 0 01-2-2v-1" />
                </svg>
                Download
              </button>
              <button @click="viewFile(file)"
                class="inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200 shadow-sm hover:shadow-md">
                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                Preview
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Files Table View -->
      <div v-else-if="viewLayout === 'table'" class="bg-white rounded-xl shadow-md border border-gray-100 overflow-hidden">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gradient-to-r from-gray-50 to-blue-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">File</th>
                <th class="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Type</th>
                <th class="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Size</th>
                <th class="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Uploader</th>
                <th class="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Date</th>
                <th class="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Access</th>
                <th class="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Downloads</th>
                <th class="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Location</th>
                <th class="px-6 py-3 text-center text-xs font-bold text-gray-700 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="file in files" :key="file.id" class="hover:bg-gray-50 transition-colors duration-150">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="flex-shrink-0 w-10 h-10 bg-gradient-to-r from-blue-100 to-purple-100 rounded-lg flex items-center justify-center">
                      <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                      </svg>
                    </div>
                    <div class="ml-3">
                      <div class="flex items-center space-x-2">
                        <div class="text-sm font-bold text-gray-900 max-w-xs truncate">
                          {{ file.title || file.originalFilename }}
                        </div>
                        <div v-if="file.uploaderId === currentUserId" 
                          class="flex items-center text-xs text-emerald-700 font-bold bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">
                          <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                          </svg>
                          Your File
                        </div>
                      </div>
                      <div v-if="file.description" class="text-xs text-gray-500 max-w-xs truncate">
                        {{ file.description }}
                      </div>
                      <div v-if="file.tags && file.tags.length > 0" class="flex flex-wrap gap-1 mt-1">
                        <span v-for="tag in file.tags.slice(0, 2)" :key="tag"
                          class="inline-flex items-center px-1.5 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-800">
                          {{ tag }}
                        </span>
                        <span v-if="file.tags.length > 2"
                          class="inline-flex items-center px-1.5 py-0.5 rounded text-xs font-medium bg-gray-100 text-gray-600">
                          +{{ file.tags.length - 2 }}
                        </span>
                      </div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-gray-900">
                    {{ file.fileTypeName || getFileTypeFromContent(file.contentType) }}
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900 font-medium">{{ formatFileSize(file.fileSize) }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mr-2">
                      <svg class="w-4 h-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                      </svg>
                    </div>
                    <div class="text-sm font-medium text-gray-900">{{ file.uploaderName }}</div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">{{ formatDate(file.uploadedAt) }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span :class="getVisibilityBadgeClass(file.visibility)"
                    class="inline-flex items-center px-2 py-1 rounded-full text-xs font-bold">
                    {{ file.visibility }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center text-sm text-gray-900">
                    <svg class="w-4 h-4 mr-1 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-4-4m4 4l4-4"/>
                    </svg>
                    <span class="font-medium">{{ file.downloadCount }}</span>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="space-y-1">
                    <div v-if="file.departmentName" class="flex items-center text-xs text-blue-700 bg-blue-50 px-2 py-1 rounded border border-blue-200">
                      <svg class="w-3 h-3 mr-1 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5"/>
                      </svg>
                      <span class="font-medium">{{ file.departmentName }}</span>
                    </div>
                    <div v-if="file.projectName" class="flex items-center text-xs text-purple-700 bg-purple-50 px-2 py-1 rounded border border-purple-200">
                      <svg class="w-3 h-3 mr-1 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2"/>
                      </svg>
                      <span class="font-medium">{{ file.projectName }}</span>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-center">
                  <div class="flex justify-center space-x-2">
                    <button @click="downloadFile(file)"
                      class="inline-flex items-center px-3 py-1.5 border-0 text-xs font-medium rounded text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200">
                      <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-4-4m4 4l4-4m5 10v1a2 2 0 01-2 2H5a2 2 0 01-2-2v-1" />
                      </svg>
                      Download
                    </button>
                    <button @click="viewFile(file)"
                      class="inline-flex items-center px-3 py-1.5 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200">
                      <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                      </svg>
                      Preview
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="mt-12 bg-white rounded-2xl shadow-lg border border-gray-100 p-8">
        <nav class="flex flex-col sm:flex-row items-center justify-between gap-6">
          <div class="flex items-center text-sm text-gray-700 bg-gradient-to-r from-gray-50 to-blue-50 px-4 py-3 rounded-xl border border-gray-200">
            <span class="flex items-center space-x-1">
              <span>Showing page</span>
              <span class="font-bold text-blue-600 mx-1">{{ currentPage + 1 }}</span>
              <span>of</span>
              <span class="font-bold text-blue-600 mx-1">{{ totalPages }}</span>
            </span>
            <span class="text-gray-500 ml-3 px-3 py-1 bg-white rounded-lg border border-gray-200">{{ totalElements }} total files</span>
          </div>
          
          <div class="flex items-center space-x-2">
            <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 0"
              class="inline-flex items-center px-5 py-3 text-sm font-bold text-gray-700 bg-white border border-gray-300 rounded-xl hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white transition-all duration-200 shadow-lg hover:shadow-xl">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
              </svg>
              Previous
            </button>

            <!-- Page Numbers -->
            <div class="hidden sm:flex items-center space-x-1">
              <template v-for="page in getVisiblePages()" :key="page">
                <button v-if="typeof page === 'number'" @click="goToPage(page - 1)" 
                  :class="[
                    'px-4 py-3 text-sm font-bold border transition-all duration-200 rounded-xl shadow-lg hover:shadow-xl',
                    currentPage === page - 1 
                      ? 'bg-gradient-to-r from-blue-600 to-purple-600 text-white border-blue-600 transform scale-110' 
                      : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50 hover:border-gray-400'
                  ]">
                  {{ page }}
                </button>
                <span v-else class="px-4 py-3 text-sm text-gray-500 font-bold">{{ page }}</span>
              </template>
            </div>

            <button @click="goToPage(currentPage + 1)" :disabled="currentPage >= totalPages - 1"
              class="inline-flex items-center px-5 py-3 text-sm font-bold text-gray-700 bg-white border border-gray-300 rounded-xl hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white transition-all duration-200 shadow-lg hover:shadow-xl">
              Next
              <svg class="w-4 h-4 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </button>
          </div>
        </nav>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { fileApi, type FileResponse, type FileFilters } from '@/services/fileApi'
import departmentApi, { type DepartmentResponse } from '@/services/departmentApi'
import projectApi, { type ProjectResponse } from '@/services/projectApi'
import fileTypeApi, { type FileType as FileTypeResponse } from '@/services/fileTypeApi'
import departmentCategoryApi, { type DepartmentCategory } from '@/services/departmentCategoryApi'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// Reactive data
const currentView = ref<'all' | 'department' | 'project'>('all')
const viewLayout = ref<'grid' | 'list' | 'table'>('grid')
const files = ref<FileResponse[]>([])
const loading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(12)

// User data
const userDepartments = ref<DepartmentResponse[]>([])
const userProjects = ref<ProjectResponse[]>([])
const fileTypes = ref<FileTypeResponse[]>([])
const departmentCategories = ref<DepartmentCategory[]>([])

// Selections
const selectedDepartmentId = ref<number | ''>('')
const selectedProjectId = ref<number | ''>('')

// Filters
const filters = reactive<FileFilters>({
  filename: '',
  departmentCategoryId: undefined,
  departmentId: undefined,
  projectId: undefined,
  fileTypeId: undefined,
  page: 0,
  size: 12
})

const sortBy = ref('uploadedAt')
const sortDirection = ref<'ASC' | 'DESC'>('DESC')

// Computed
const currentUserId = computed(() => authStore.user?.id)

// Check if any filters are currently active
const hasActiveFilters = computed(() => {
  return !!(filters.filename || 
           filters.departmentCategoryId || 
           filters.departmentId || 
           filters.projectId || 
           filters.fileTypeId)
})

// Available department categories based on current view and selected department
const availableDepartmentCategories = computed(() => {
  if (currentView.value === 'department' && selectedDepartmentId.value) {
    return departmentCategories.value.filter(cat => cat.departmentId === Number(selectedDepartmentId.value))
  }
  return departmentCategories.value
})

// Debounced search
let searchTimeout: number
const debouncedSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    applyFilters()
  }, 500)
}

// Watchers
watch(currentView, () => {
  selectedDepartmentId.value = ''
  selectedProjectId.value = ''
  // Clear filters when view changes
  filters.departmentCategoryId = undefined
  filters.departmentId = undefined
  filters.projectId = undefined
  clearFilters()
})

// Methods
const loadSharedFiles = async () => {
  loading.value = true
  try {
    const params = {
      ...filters,
      page: currentPage.value,
      size: pageSize.value,
      sortBy: sortBy.value,
      sortDirection: sortDirection.value
    }

    console.log('Loading shared files with params:', params)
    console.log('Current view:', currentView.value)
    console.log('Selected department ID:', selectedDepartmentId.value)
    console.log('Selected project ID:', selectedProjectId.value)

    // Always use the general shared files endpoint with filters
    const response = await fileApi.getSharedFiles(params)

    console.log('Shared files response:', response)

    if (response.success && response.data) {
      files.value = response.data.content
      totalPages.value = response.data.totalPages
      totalElements.value = response.data.totalElements
      console.log('Loaded files:', files.value.length)
    } else {
      console.warn('Failed to load shared files:', response)
    }
  } catch (error) {
    console.error('Error loading shared files:', error)
  } finally {
    loading.value = false
  }
}

// Load department categories for a specific department
const loadDepartmentCategories = async (departmentId: number) => {
  try {
    console.log(`Loading categories for department ID: ${departmentId}`)
    const response = await departmentCategoryApi.getCategoriesByDepartment(departmentId)
    console.log(`Department ${departmentId} categories response:`, response)
    
    if (response.success && response.data && Array.isArray(response.data) && response.data.length > 0) {
      console.log(`Found ${response.data.length} categories for department ${departmentId}`)
      // Add categories to the existing array, avoiding duplicates
      const newCategories = response.data.filter(newCat => 
        !departmentCategories.value.some(existingCat => existingCat.id === newCat.id)
      )
      departmentCategories.value.push(...newCategories)
      console.log(`Added ${newCategories.length} new categories for department ${departmentId}`)
    } else if (Array.isArray(response) && response.length > 0) {
      // Handle direct array response
      console.log(`Found ${response.length} categories (direct array) for department ${departmentId}`)
      const newCategories = response.filter(newCat => 
        !departmentCategories.value.some(existingCat => existingCat.id === newCat.id)
      )
      departmentCategories.value.push(...newCategories)
    } else {
      console.log(`No categories found for department ${departmentId}`)
    }
  } catch (error) {
    console.error(`Failed to load department categories for department ${departmentId}:`, error)
  }
}

const loadUserData = async () => {
  try {
    console.log('Starting to load user data...')
    
    // Load user's departments and projects
    const [deptResponse, projResponse, typeResponse] = await Promise.all([
      departmentApi.getUserDepartments(),
      projectApi.getUserProjects(),
      fileTypeApi.getAllFileTypes()
    ])

    console.log('Raw API responses:')
    console.log('Department response:', deptResponse)
    console.log('Project response:', projResponse)
    console.log('File type response:', typeResponse)
    console.log('File type response type:', typeof typeResponse)
    console.log('File type response keys:', typeResponse ? Object.keys(typeResponse) : 'null')
    console.log('Is file type response an array?', Array.isArray(typeResponse))

    // Backend returns ApiResponse<List<Department>> format: { success, message, data }
    if (deptResponse?.success && deptResponse.data) {
      userDepartments.value = deptResponse.data
      console.log('Departments set:', userDepartments.value)
    } else {
      console.warn('Department response structure unexpected:', deptResponse)
    }

    // Backend returns ApiResponse<List<Project>> format: { success, message, data }
    if (projResponse?.success && projResponse.data) {
      userProjects.value = projResponse.data
      console.log('Projects set:', userProjects.value)
    } else {
      console.warn('Project response structure unexpected:', projResponse)
    }

    // Handle different possible response structures for file types
    if (typeResponse?.success && typeResponse.data && Array.isArray(typeResponse.data)) {
      // Standard API response with success flag
      console.log('Using standard API response structure for file types')
      fileTypes.value = typeResponse.data
      console.log('File types set:', fileTypes.value)
    } else if (Array.isArray(typeResponse)) {
      // Direct array response
      console.log('Using direct array response structure for file types')
      fileTypes.value = typeResponse
      console.log('File types set:', fileTypes.value)
    } else {
      console.warn('File type response structure unexpected:', typeResponse)
      // Try to extract data anyway if it exists
      if (typeResponse && typeof typeResponse === 'object' && typeResponse.data) {
        fileTypes.value = typeResponse.data
        console.log('File types set from fallback:', fileTypes.value)
      }
    }

    // Load department categories for all user departments
    if (userDepartments.value.length > 0) {
      console.log('Loading department categories for all user departments')
      for (const department of userDepartments.value) {
        await loadDepartmentCategories(department.id)
      }
    }
  } catch (error: any) {
    console.error('Error loading user data:', error)
    if (error?.response) {
      console.error('Error response:', error.response.data)
      console.error('Error status:', error.response.status)
    }
    if (error?.request) {
      console.error('Error request:', error.request)
    }
  }
  finally {
    console.log('Final user data state:')
    console.log('File types:', fileTypes.value)
    console.log('User departments:', userDepartments.value)
    console.log('User projects:', userProjects.value)
    console.log('Department categories:', departmentCategories.value)
  }
}

const onDepartmentChange = async () => {
  console.log('Department changed to:', selectedDepartmentId.value)
  
  // Clear category filter when department changes
  filters.departmentCategoryId = undefined
  
  // Set the department filter
  filters.departmentId = selectedDepartmentId.value ? Number(selectedDepartmentId.value) : undefined
  
  console.log('Updated filters after department change:', filters)
  
  // Load categories for the selected department
  if (selectedDepartmentId.value) {
    await loadDepartmentCategories(Number(selectedDepartmentId.value))
  }
  
  currentPage.value = 0
  loadSharedFiles()
}

const onProjectChange = () => {
  console.log('Project changed to:', selectedProjectId.value)
  
  // Set the project filter
  filters.projectId = selectedProjectId.value ? Number(selectedProjectId.value) : undefined
  
  console.log('Updated filters after project change:', filters)
  
  currentPage.value = 0
  loadSharedFiles()
}

const applyFilters = () => {
  currentPage.value = 0
  loadSharedFiles()
}

const clearFilters = () => {
  filters.filename = ''
  filters.departmentCategoryId = undefined
  filters.departmentId = undefined
  filters.projectId = undefined
  filters.fileTypeId = undefined
  currentPage.value = 0
  loadSharedFiles()
}

const toggleSortDirection = () => {
  sortDirection.value = sortDirection.value === 'DESC' ? 'ASC' : 'DESC'
  applyFilters()
}

const goToPage = (page: number) => {
  currentPage.value = page
  loadSharedFiles()
}

const getVisiblePages = () => {
  const delta = 2
  const range = []
  const rangeWithDots = []

  for (let i = Math.max(2, currentPage.value + 1 - delta);
       i <= Math.min(totalPages.value - 1, currentPage.value + 1 + delta);
       i++) {
    range.push(i)
  }

  if (currentPage.value + 1 - delta > 2) {
    rangeWithDots.push(1, '...')
  } else {
    rangeWithDots.push(1)
  }

  rangeWithDots.push(...range)

  if (currentPage.value + 1 + delta < totalPages.value - 1) {
    rangeWithDots.push('...', totalPages.value)
  } else {
    rangeWithDots.push(totalPages.value)
  }

  return rangeWithDots
}

const downloadFile = async (file: FileResponse) => {
  try {
    const blob = await fileApi.downloadFile(file.id)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = file.originalFilename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Error downloading file:', error)
  }
}

const viewFile = (file: FileResponse) => {
  // Open file details or preview modal
  console.log('View file:', file)
}

const getNoFilesMessage = () => {
  if (currentView.value === 'department' && !selectedDepartmentId.value) {
    return 'Please select a department to view files.'
  }
  if (currentView.value === 'project' && !selectedProjectId.value) {
    return 'Please select a project to view files.'
  }
  return 'No files found matching your criteria.'
}

const getVisibilityBadgeClass = (visibility: string) => {
  switch (visibility) {
    case 'PUBLIC':
      return 'bg-green-100 text-green-800'
    case 'DEPARTMENT':
      return 'bg-blue-100 text-blue-800'
    case 'PROJECT':
      return 'bg-purple-100 text-purple-800'
    case 'PRIVATE':
      return 'bg-gray-100 text-gray-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const getVisibilityTextColor = (visibility: string) => {
  switch (visibility) {
    case 'PUBLIC':
      return 'text-green-700'
    case 'DEPARTMENT':
      return 'text-blue-700'
    case 'PROJECT':
      return 'text-purple-700'
    case 'PRIVATE':
      return 'text-gray-700'
    default:
      return 'text-gray-700'
  }
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const getFileTypeFromContent = (contentType: string): string => {
  if (contentType.startsWith('image/')) return 'Image'
  if (contentType.startsWith('video/')) return 'Video'
  if (contentType.startsWith('audio/')) return 'Audio'
  if (contentType === 'application/pdf') return 'PDF'
  if (contentType.includes('document') || contentType.includes('word')) return 'Document'
  if (contentType.includes('spreadsheet') || contentType.includes('excel')) return 'Spreadsheet'
  return 'File'
}

// Lifecycle
onMounted(async () => {
  await loadUserData()
  await loadSharedFiles()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  line-clamp: 3;
  overflow: hidden;
}

/* Enhanced hover animations */
.group:hover .group-hover\:animate-bounce {
  animation: bounce 1s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}

/* Smooth pulse animation for loading dots */
@keyframes pulse-delay {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* Enhanced gradient animations */
@keyframes gradient-shift {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.animate-gradient {
  background-size: 200% 200%;
  animation: gradient-shift 3s ease infinite;
}

/* Custom scrollbar styling */
::-webkit-scrollbar {
  width: 10px;
}

::-webkit-scrollbar-track {
  background: linear-gradient(to bottom, #f1f5f9, #e2e8f0);
  border-radius: 5px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, #cbd5e1, #94a3b8);
  border-radius: 5px;
  border: 2px solid #f1f5f9;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, #94a3b8, #64748b);
}

/* Enhanced focus styles */
button:focus-visible,
select:focus-visible,
input:focus-visible {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}

/* Smooth transitions for all interactive elements */
button,
select,
input,
.group {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Enhanced card hover effects */
.group:hover {
  transform: translateY(-4px) scale(1.02);
}

/* Loading animation improvements */
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* File type badge animations */
.badge-enter-active,
.badge-leave-active {
  transition: all 0.3s ease;
}

.badge-enter-from,
.badge-leave-to {
  opacity: 0;
  transform: scale(0.8);
}
</style>
