<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>つぶやく</title>
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
  <style>
    body { font-family: sans-serif; max-width: 600px; margin: 2rem auto; padding: 0 1rem; }
    #map { height: 300px; margin: 0.5rem 0 1rem; border: 1px solid #ccc; border-radius: 8px; }
    textarea { width: 100%; box-sizing: border-box; padding: 8px; font-size: 15px; }
    button { margin-top: 0.75rem; padding: 8px 24px; font-size: 15px; cursor: pointer; }
    #location-label { font-size: 13px; color: #666; margin-bottom: 0.5rem; }
  </style>
</head>
<body>

<h2>つぶやく</h2>

<form action="post" method="post">
  <textarea name="text" rows="3" placeholder="いまどこで何してる？" required></textarea>

  <p>場所を選ぶ（地図をクリック）</p>
  <div id="map"></div>
  <p id="location-label">📍 場所未選択</p>

  <!-- 地図クリックで緯度経度をここにセット -->
  <input type="hidden" name="latitude"  id="latitude"  value="">
  <input type="hidden" name="longitude" id="longitude" value="">

  <button type="submit">つぶやく</button>
  <a href="timeline" style="margin-left:1rem;">タイムラインへ</a>
</form>

<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script>
  const map = L.map('map').setView([35.6812, 139.7671], 11);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
  }).addTo(map);

  let marker = null;

  map.on('click', function(e) {
    const lat = e.latlng.lat.toFixed(6);
    const lng = e.latlng.lng.toFixed(6);

    document.getElementById('latitude').value  = lat;
    document.getElementById('longitude').value = lng;
    document.getElementById('location-label').textContent
        = '📍 ' + lat + ', ' + lng;

    if (marker) map.removeLayer(marker);
    marker = L.marker(e.latlng).addTo(map);
  });
</script>

</body>
</html>