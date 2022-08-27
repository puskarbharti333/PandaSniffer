# PandaSniffer
## Panda Sniffer is an open source app which lets we monitor andexport thenetwork traffic of wer device. The app simulates a VPN to achieve non-rootcapture but, contrary to a VPN, the traffic is processed locally into thedevice. 
### Features: 
### Log and examine the connections made by user and systemapps
### Extract the SNI, DNS query, HTTP request, HTTP URL and the remoteIPaddress
### Create rules to filter out the good traffic and easily spot anomalies Dump the traffic into a PCAP file, download it froma browser, or streamit to a remote receiver for real time analysis (e.g. Wireshark)
### Use the app in combination with mitmproxy to decrypt HTTPS/TLStraffic (technical knowledge required) 
### On rooted devices, capture the traffic while other VPN apps are running

## It is an Android app contains 2 primary functions proxy and packet capturing, with no root requirement. 
### 1. Proxy - Some kind of like ShadowSocket, we can specify the network request madebyother apps on wer phone to go through the proxy server we designated. 
### 2. Packet capturing - It can capture IP packets and store in a PCAP format which can be openedbyWireshark.
