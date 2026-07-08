from pythonosc import udp_client
import time

# Conecta no IP local na porta 12000 que seu MAPPA está escutando
client = udp_client.SimpleUDPClient("127.0.0.1", 12000)

while True:
    # Simula uma coordenada X, Y enviada pelo MediaPipe
    x_fake = 320.0
    y_fake = 240.0

    # Envia pro padrão que colocamos no método oscEvent
    client.send_message("/mediapipe/target", [x_fake, y_fake])
    time.sleep(0.03) # ~30 FPS