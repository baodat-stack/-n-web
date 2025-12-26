
    document.getElementById("searchBox").addEventListener("input", function () {
                    const query = this.value;
                    if (query.length > 0) {
        fetch(`/Home/GetSuggestions?query=${query}`)
            .then(res => res.json())
            .then(data => {
                const bucket = document.querySelector("#suggestions .result-bucket");
                bucket.innerHTML = "";
                data.forEach(item => {
                    const li = document.createElement("li");
                    li.className = "result-entry";
                    li.innerHTML = `
                                            <a href="${item.link}" class="result-link">
                                                <div class="media-left">
                                                    <img src="${item.imageUrl}" class="media-object">
                                                </div>
                                                <div class="media-body">
                                                    <strong>${item.name}</strong><br>
                                                    <span>${item.price ? item.price + "₫" : ""}</span>
                                                </div>
                                            </a>
                                        `;
                    bucket.appendChild(li);
                });

            });
                    } else {
        document.querySelector("#suggestions .result-bucket").innerHTML = "";
                    }
    });
