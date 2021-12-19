function getWeather(city) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var response = this.responseXML;

            var temperature = response.getElementsByTagName("temperature")[0];
            $("#temp").text(temperature.getAttribute('value') + " ºC");
            $("#temp_min").text(temperature.getAttribute('min') + " ºC");
            $("#temp_max").text(temperature.getAttribute('max') + " ºC");
            $("#condition-img").html('<img src="http://openweathermap.org/img/wn/' + response.getElementsByTagName("weather")[0].getAttribute('icon') + '@2x.png"></img>');
            var sun = response.getElementsByTagName("sun")[0];
            $("#sunrise").text(new Date(sun.getAttribute('rise') + 'Z').toLocaleTimeString());
            $("#sunset").text(new Date(sun.getAttribute('set') + 'Z').toLocaleTimeString());
            coord = response.getElementsByTagName("coord")[0];
            $("#lat").text(coord.getAttribute('lat'));
            $("#lon").text(coord.getAttribute('lon'));

            $("#erro").hide();
            $("#result").show();
            initMap(parseFloat(coord.getAttribute('lat')), parseFloat(coord.getAttribute('lon')));

        } else if (this.readyState == 4 && this.status == 404) {
            $("#err-msg").text("Cidade não encontrada!");
            $("#err").show();
            $("#result").hide();
        } else if (this.readyState == 4) {
            $("#erro-msg").text("Erro ao buscar dados!");
            $("#erro").show();
            $("#result").hide();
        }

    };
    xhttp.open("GET", "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&lang=pt_br&mode=xml&appid=323c712fe8bb40a5a669d8f20a32c13b", true);
    xhttp.send();
}

function initMap(lat = 1, lon = 1) {
    const uluru = { lat: lat, lng: lon };
    const map = new google.maps.Map(document.getElementById("map"), {
        zoom: 13,
        center: uluru,
    });
    const marker = new google.maps.Marker({
        position: uluru,
        map: map,
    });
}

$(document).ready(function() {
    $("#search").click(function() {
        var cidade = $("#city").val();
        getWeather(cidade);
    });

    $("#city").keypress(function(e) {
        if (e.keyCode == 13) {
            $("#search").click();
        }
    });
});