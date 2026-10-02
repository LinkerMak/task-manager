#!/usr/bin/env bash
set -euo pipefail

readonly BACKEND_URL="${BACKEND_URL:-http://localhost:8080}"
readonly MAILPIT_URL="${MAILPIT_URL:-http://localhost:8025}"

readonly RUN_ID="${GITHUB_RUN_ID:-local}"
readonly RUN_ATTEMPT="${GITHUB_RUN_ATTEMPT:-1}"

readonly EMAIL="smoke-${RUN_ID}-${RUN_ATTEMPT}@task-manager.ci"
readonly PASSWORD="SmokePassword123"

response_headers="$(mktemp)"
response_body="$(mktemp)"
me_response="$(mktemp)"

cleanup() {
  rm -f "$response_headers" "$response_body" "$me_response"
}

trap cleanup EXIT

echo "Registering smoke-test user: ${EMAIL}"

registration_status="$(
  curl \
    --silent \
    --show-error \
    --output "$response_body" \
    --dump-header "$response_headers" \
    --write-out "%{http_code}" \
    --request POST \
    --header "Content-Type: application/json" \
    --data "{\"email\":\"${EMAIL}\",\"password\":\"${PASSWORD}\"}" \
    "${BACKEND_URL}/users"
)"

if [[ "$registration_status" != "200" ]]; then
  echo "Registration failed: expected HTTP 200, got HTTP ${registration_status}."

  echo "Response headers:"
  cat "$response_headers"

  echo "Response body:"
  cat "$response_body"

  exit 1
fi

authorization_header="$(
  tr -d '\r' < "$response_headers" |
    grep -i '^Authorization: Bearer ' |
    head -n 1 ||
    true
)"

if [[ -z "$authorization_header" ]]; then
  echo "Registration returned HTTP 200 but no Authorization: Bearer header."

  echo "Response headers:"
  cat "$response_headers"

  exit 1
fi

jwt_token="$(
  printf '%s' "$authorization_header" |
    sed -E 's/^[Aa]uthorization:[[:space:]]*[Bb]earer[[:space:]]+//'
)"

if [[ -z "$jwt_token" ]]; then
  echo "Authorization header exists but JWT token could not be extracted."
  exit 1
fi

echo "Checking authenticated endpoint: GET /users/me"

current_user_status="$(
  curl \
    --silent \
    --show-error \
    --output "$me_response" \
    --write-out "%{http_code}" \
    --header "Authorization: Bearer ${jwt_token}" \
    "${BACKEND_URL}/users/me"
)"

if [[ "$current_user_status" != "200" ]]; then
  echo "Authenticated request failed: expected HTTP 200, got HTTP ${current_user_status}."

  echo "Response body:"
  cat "$me_response"

  exit 1
fi

if ! grep -Fq "$EMAIL" "$me_response"; then
  echo "GET /users/me returned HTTP 200, but response does not contain the registered email."

  echo "Response body:"
  cat "$me_response"

  exit 1
fi

echo "Waiting for the welcome email in Mailpit."

for attempt in {1..20}; do
  messages="$(
    curl \
      --silent \
      --show-error \
      "${MAILPIT_URL}/api/v1/messages" ||
      true
  )"

  if printf '%s' "$messages" | grep -Fq "$EMAIL"; then
    echo "Smoke-test passed."
    echo "User '${EMAIL}' was registered, JWT was accepted, and welcome email reached Mailpit."
    exit 0
  fi

  echo "Welcome email not found yet; attempt ${attempt}/20."
  sleep 1
done

echo "Welcome email for '${EMAIL}' was not found in Mailpit within 20 seconds."
exit 1