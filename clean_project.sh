#!/bin/bash

# This script removes Gradle build artifacts and caches from the project directory.

echo "Cleaning Gradle project..."

# Remove the build directory
echo "Removing build directory..."
rm -rf build

# Remove the .gradle directory (project-specific cache)
echo "Removing .gradle directory..."
rm -rf .gradle

echo "Project cleaned successfully."