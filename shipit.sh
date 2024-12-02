#!/usr/bin/env bash

function set_fail_on_error() {
  # exit immediately if any command returns a non-zero status
  set -o errexit
  
  # exit if using an undefined variable
  set -o nounset
  
  # fail if anything in pipeline fails, not just the last command
  set -o pipefail
}

function show_success() {
    echo -e "$(cat shipit-success-ascii-art.txt)"
}

function go_to_root_directory() {
    local -r root_directory=$(git rev-parse --show-toplevel)
    cd "$root_directory"
}

function fail_for_uncommitted_files() {
    local -r unstaged_files_count=$(git status --porcelain | wc -l)
    local -r trimmed_unstaged_files_count=$(echo -e -n "$unstaged_files_count" | tr -d ' ')

    if [ "$trimmed_unstaged_files_count" != "0" ]; then
        local -r unstaged_files=$(git status --porcelain)
        echo -e "\\nyou forgot to stage these files:\\n\\n${unstaged_files}"
        return 1
    fi
}

function build_images() {
    docker compose build
}

function deploy_to_local_docker() {
  docker compose up --detach
}

function run_tests() {
  pushd analytics-api
  ./mvnw clean test
  popd
  
  pushd job-mgmt-api
  ./mvnw clean test -Dgroups="!end-to-end-test"
  popd
  
  pushd manager
  ./mvnw clean test
  popd
  
  pushd worker
  ./mvnw clean test
  popd
}

function run_e2e_tests() {
  pushd job-mgmt-api
  ./mvnw clean test -Dgroups="end-to-end-test"
  popd
}

function main() {
  set_fail_on_error
  go_to_root_directory
  fail_for_uncommitted_files
  run_tests
  build_images
  deploy_to_local_docker
  run_e2e_tests
  show_success  
}

main
