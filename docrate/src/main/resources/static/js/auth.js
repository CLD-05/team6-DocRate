async function fetchWithAuth(url, options = {}) {
    const finalOptions = {
        ...options,
        credentials: 'include'
    };

    const response = await fetch(url, finalOptions);
    return response;
}