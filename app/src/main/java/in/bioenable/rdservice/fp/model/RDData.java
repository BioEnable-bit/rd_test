package in.bioenable.rdservice.fp.model;

public class RDData {

//old certificate
//    private static final String UIDAI_CERT_P =
//            "MIIFVDCCBDygAwIBAgIEAMwvlTANBgkqhkiG9w0BAQsFADCBkDELMAkGA1UEBhMC\n" +
//            "    SU4xKjAoBgNVBAoTIWVNdWRocmEgQ29uc3VtZXIgU2VydmljZXMgTGltaXRlZDEd\n" +
//            "    MBsGA1UECxMUQ2VydGlmeWluZyBBdXRob3JpdHkxNjA0BgNVBAMTLWUtTXVkaHJh\n" +
//            "    IFN1YiBDQSBmb3IgQ2xhc3MgMyBPcmdhbmlzYXRpb24gMjAxNDAeFw0xNjEyMzAx\n" +
//            "    MTA2MTlaFw0xOTEyMzAxMTA2MTlaMIGCMQswCQYDVQQGEwJJTjEOMAwGA1UEChMF\n" +
//            "    VUlEQUkxGjAYBgNVBAsTEVRlY2hub2xvZ3kgQ2VudHJlMQ8wDQYDVQQREwY1NjAw\n" +
//            "    OTIxEjAQBgNVBAgTCUthcm5hdGFrYTEiMCAGA1UEAxMZU2lyaXNoIENob3VkaGFy\n" +
//            "    eSBKYXJ1YnVsYTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAN9Hh/lI\n" +
//            "    m/V7eP8Tk9p27BKhHvITxsr7lJTO0TFaeDgNjuec8UPHsasIkPBpWZw9mOqwbC8A\n" +
//            "    1uwQviyBTpBamk2HIKyF6NlKMg3Ihdot3+z3k3iT9E6PbJGTmzT1JrEK4eZ2ULn2\n" +
//            "    TmXzRofkZCMV77F/263e1AJtVyJLWA3ZwtPfYBpBDAGUYQSOddQKdzcEgMlANo7U\n" +
//            "    Ha2jzuMc8doPDV2EF/YuQfedru9gyvtHZMVHNiyFIIonABI5A6BZbspNY/JDv7iw\n" +
//            "    68vfmqYlLMIbV3c5ca6cFOH8gGyAAA6jeRFu90rKWiwKoa3tkgoQM/x4lhGu6dG+\n" +
//            "    fKZ1VHCWWWHPjbsCAwEAAaOCAcAwggG8MCEGA1UdEQQaMBiBFnNpcmlzaC5qc0B1\n" +
//            "    aWRhaS5uZXQuaW4wEwYDVR0jBAwwCoAITNG9KhFIBNMwHQYDVR0OBBYEFIa84oX1\n" +
//            "    K3hof37+oh3sTHXGT47/MA4GA1UdDwEB/wQEAwIFIDCBjAYDVR0gBIGEMIGBMC0G\n" +
//            "    BmCCZGQCAzAjMCEGCCsGAQUFBwICMBUaE0NsYXNzIDMgQ2VydGlmaWNhdGUwUAYH\n" +
//            "    YIJkZAEIAjBFMEMGCCsGAQUFBwIBFjdodHRwOi8vd3d3LmUtbXVkaHJhLmNvbS9y\n" +
//            "    ZXBvc2l0b3J5L2Nwcy9lLU11ZGhyYV9DUFMucGRmMHsGCCsGAQUFBwEBBG8wbTAk\n" +
//            "    BggrBgEFBQcwAYYYaHR0cDovL29jc3AuZS1tdWRocmEuY29tMEUGCCsGAQUFBzAC\n" +
//            "    hjlodHRwOi8vd3d3LmUtbXVkaHJhLmNvbS9yZXBvc2l0b3J5L2NhY2VydHMvQzNP\n" +
//            "    U0NBMjAxNC5jcnQwRwYDVR0fBEAwPjA8oDqgOIY2aHR0cDovL3d3dy5lLW11ZGhy\n" +
//            "    YS5jb20vcmVwb3NpdG9yeS9jcmxzL0MzT1NDQTIwMTQuY3JsMA0GCSqGSIb3DQEB\n" +
//            "    CwUAA4IBAQCtIELoC2dLA9cP4FSV426Pda0hdhWEnOTAnF9DA0RUfq47ujUB/edq\n" +
//            "    g8CvEl5KxyMftsNaXIEUoh4myToUATR1ybxpq6hKKhOM3JYdq3u+5S8D4wXd1HIn\n" +
//            "    rNgBnkta0af578yIfRjt6KDyHZUVcG57zxbKbRQvWqnQy/00abLwpwAeEvjoDc7f\n" +
//            "    5Re9Soz6RsUVckNztdZDaXetYQ/Mu/e6qCToH5A4Vgbu0sseQsfBOKZtVZA5p6Op\n" +
//            "    MvIbDwwdec97cVeoEd2lI+8aGY6zm9ttXvTxsEJtxiNmTJKc0077IaLcpqhDte6V\n" +
//            "    TSAK0qOiiOMa4Od2jhg6syGENFbMTiXg";


    private final String UIDAI_CERT_P =
            "MIIFujCCBKKgAwIBAgIEARH+GDANBgkqhkiG9w0BAQsFADCBkDELMAkGA1UEBhMC\n" +
                    "SU4xKjAoBgNVBAoTIWVNdWRocmEgQ29uc3VtZXIgU2VydmljZXMgTGltaXRlZDEd\n" +
                    "MBsGA1UECxMUQ2VydGlmeWluZyBBdXRob3JpdHkxNjA0BgNVBAMTLWUtTXVkaHJh\n" +
                    "IFN1YiBDQSBmb3IgQ2xhc3MgMyBPcmdhbmlzYXRpb24gMjAxNDAeFw0xOTEwMjIx\n" +
                    "MzUwMTJaFw0yMjEwMjExMzUwMTJaMIG+MQswCQYDVQQGEwJJTjEOMAwGA1UEChMF\n" +
                    "VUlEQUkxGjAYBgNVBAsTEVRlY2hub2xvZ3kgQ2VudHJlMQ8wDQYDVQQREwY1NjAw\n" +
                    "OTIxEjAQBgNVBAgTCUtBUk5BVEFLQTFJMEcGA1UEBRNAODdiNzU2ZjQ5ZWZlY2Jh\n" +
                    "MTczNzllOTY5N2JhMmNmMzFkYTdlOGY4NGE2ZThmNGRhOGQwNWIxODNmYjVkMWFk\n" +
                    "OTETMBEGA1UEAxMKQU5VUCBLVU1BUjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCC\n" +
                    "AQoCggEBAKsLzL3ZBNu4Czfqa7XarC63uphHTc/mWnDAKHZ0Dh8mBGVb+xeM8zVS\n" +
                    "9y6VLsciBZ/O6/qXQwfefWyjL2MLt/0LpEE4cG7VLV1C9OYK6y2MbrT9nwzN+pAM\n" +
                    "zcWTNiiee6W6LaKjXcOqs3Dzgzd19KW1Zk5U6blISRH5NCQ83srTAPMll1xMkzbf\n" +
                    "PCluvYlR7RtODyNQP5EwvuTncpAVi+7r1weV431LvjeuDajDfV494knt/5XkJha5\n" +
                    "7MtfzeuQbVXfHzHTgkubcpCjSjMHhxOeBisjtcFtrTi4K2RQ5njWEkvAyXSmDWb1\n" +
                    "XJx6w+p69ezN9KqwJubbShzd9PVixZcCAwEAAaOCAeowggHmMCIGA1UdEQQbMBmB\n" +
                    "F2FudXAua3VtYXJAdWlkYWkubmV0LmluMBMGA1UdIwQMMAqACEzRvSoRSATTMB0G\n" +
                    "A1UdDgQWBBRgl7mQb3UttrwwwusTkidEbBmwyzAMBgNVHRMBAf8EAjAAMA4GA1Ud\n" +
                    "DwEB/wQEAwIFIDAZBgNVHSUBAf8EDzANBgsrBgEEAYI3CgMEATCBjAYDVR0gBIGE\n" +
                    "MIGBMC0GBmCCZGQCAzAjMCEGCCsGAQUFBwICMBUaE0NsYXNzIDMgQ2VydGlmaWNh\n" +
                    "dGUwUAYHYIJkZAEIAjBFMEMGCCsGAQUFBwIBFjdodHRwOi8vd3d3LmUtbXVkaHJh\n" +
                    "LmNvbS9yZXBvc2l0b3J5L2Nwcy9lLU11ZGhyYV9DUFMucGRmMHsGCCsGAQUFBwEB\n" +
                    "BG8wbTAkBggrBgEFBQcwAYYYaHR0cDovL29jc3AuZS1tdWRocmEuY29tMEUGCCsG\n" +
                    "AQUFBzAChjlodHRwOi8vd3d3LmUtbXVkaHJhLmNvbS9yZXBvc2l0b3J5L2NhY2Vy\n" +
                    "dHMvQzNPU0NBMjAxNC5jcnQwRwYDVR0fBEAwPjA8oDqgOIY2aHR0cDovL3d3dy5l\n" +
                    "LW11ZGhyYS5jb20vcmVwb3NpdG9yeS9jcmxzL0MzT1NDQTIwMTQuY3JsMA0GCSqG\n" +
                    "SIb3DQEBCwUAA4IBAQBMONK4yQPA9QPSwaSPsSygegmLAbAnWxZVeKF7iARnRj2e\n" +
                    "KvC4kPP9fB+ZjeHnsPAbjRO3lLSKdqctMGCsQlbVwrnHKYhEdMHADwd1Q84bUwZf\n" +
                    "CKfPVkz7A1pky/SNyCWsuFqo1YplmBJxHLO7PKiTpt15K+MqOrZI1FLyzDfh5DSd\n" +
                    "g77YqUkqbgcqBjuiH7Zc8EtYj76olOXn+B4PHtZi5BG4IvGV+f/WtnHGLi700461\n" +
                    "CgAWQ59/TzCsESmOpfAUpAlSBhKUamR/YhikeBG0kncxzPLOm+A8Thi2mZhSo6Jo\n" +
                    "V+YRES1dPFxXa2c4/iD7RG1uHghzIz+vF4dQHNrF";

//   old certificate
//
//    private final String UIDAI_CERT_PP =
//            "MIIFkTCCBHmgAwIBAgIEAOATLTANBgkqhkiG9w0BAQsFADCBkDELMAkGA1UEBhMC\n" +
//            "SU4xKjAoBgNVBAoTIWVNdWRocmEgQ29uc3VtZXIgU2VydmljZXMgTGltaXRlZDEd\n" +
//            "MBsGA1UECxMUQ2VydGlmeWluZyBBdXRob3JpdHkxNjA0BgNVBAMTLWUtTXVkaHJh\n" +
//            "IFN1YiBDQSBmb3IgQ2xhc3MgMyBPcmdhbmlzYXRpb24gMjAxNDAeFw0xNzEwMzEx\n" +
//            "MzQwNTNaFw0yMDEwMzAxMzQwNTNaMIG+MQswCQYDVQQGEwJJTjEOMAwGA1UEChMF\n" +
//            "VUlEQUkxGjAYBgNVBAsTEVRlY2hub2xvZ3kgQ2VudHJlMQ8wDQYDVQQREwY1NjAw\n" +
//            "OTIxEjAQBgNVBAgTCUthcm5hdGFrYTFJMEcGA1UEBRNAODdiNzU2ZjQ5ZWZlY2Jh\n" +
//            "MTczNzllOTY5N2JhMmNmMzFkYTdlOGY4NGE2ZThmNGRhOGQwNWIxODNmYjVkMWFk\n" +
//            "OTETMBEGA1UEAxMKQW51cCBLdW1hcjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCC\n" +
//            "AQoCggEBAOlums2zjLx4cBx7MJGSN2hunAo3wuRu9Tk5bSkstTmIo4/Som8st2H5\n" +
//            "Zy9YBpWNuqkT29LCqtzvJKu1WLClnRyrIWuEVxCerdJCb9+PXjjZwdUIIrGMyo0r\n" +
//            "UzysN62ybPhYeC1Ab+eQge9JEmQsdrJJ9Mya3A/9/FdVPRG3pIPu9tvodYedtt/I\n" +
//            "lhhDzzC7wfKKHXDY8ydU06anf+StC3GPiroqNHVKOWTI3ZiYKk9Qxnw9lOc3w4Ty\n" +
//            "fYSO/NR3/2OeFfjFva+uJrnm2rBZmlb+ScmIkuY13os7OFA9vjLPiGkjCOpAaQZD\n" +
//            "g8JzHiDSrKc01icLc/To/Dl/dVaLM9cCAwEAAaOCAcEwggG9MCIGA1UdEQQbMBmB\n" +
//            "F2FudXAua3VtYXJAdWlkYWkubmV0LmluMBMGA1UdIwQMMAqACEzRvSoRSATTMB0G\n" +
//            "A1UdDgQWBBQtqQG/bJDE3ZRmfzxsgLEIcF7BfzAOBgNVHQ8BAf8EBAMCBSAwgYwG\n" +
//            "A1UdIASBhDCBgTAtBgZggmRkAgMwIzAhBggrBgEFBQcCAjAVGhNDbGFzcyAzIENl\n" +
//            "cnRpZmljYXRlMFAGB2CCZGQBCAIwRTBDBggrBgEFBQcCARY3aHR0cDovL3d3dy5l\n" +
//            "LW11ZGhyYS5jb20vcmVwb3NpdG9yeS9jcHMvZS1NdWRocmFfQ1BTLnBkZjB7Bggr\n" +
//            "BgEFBQcBAQRvMG0wJAYIKwYBBQUHMAGGGGh0dHA6Ly9vY3NwLmUtbXVkaHJhLmNv\n" +
//            "bTBFBggrBgEFBQcwAoY5aHR0cDovL3d3dy5lLW11ZGhyYS5jb20vcmVwb3NpdG9y\n" +
//            "eS9jYWNlcnRzL0MzT1NDQTIwMTQuY3J0MEcGA1UdHwRAMD4wPKA6oDiGNmh0dHA6\n" +
//            "Ly93d3cuZS1tdWRocmEuY29tL3JlcG9zaXRvcnkvY3Jscy9DM09TQ0EyMDE0LmNy\n" +
//            "bDANBgkqhkiG9w0BAQsFAAOCAQEAOVx0mfOydPPGd3YW5qd28pWRPR3uq9hANq4e\n" +
//            "c/Fqe1xg3dzlKWBWcUOLQqKuD5DEb6MYYzBCMe7iinDDzTNQFQq1y5rO+GD4WxN/\n" +
//            "7mDkMnpdUju+vKi7AEF2wuqZBuNoSvdRqsq7ZgzLZXrdYwYLXaxpxcQ0QlzhECdv\n" +
//            "/K2AGf/wv8nh/BIckHZuJSs5MrCZtiKS84tpXHHHL/Cjd0y3UO+35VvxFOZ50BQr\n" +
//            "i4XEIDYQd0liyHwTWkv7CoxTYHO9DPPttd1s9nsY1mHSGGWLWUoy3v1yc4iFaw28\n" +
//            "GXUxpP5A9BfwWFbeqaHx8Tcn0x8YB1r/dAQlE0G/VTsEiugxig==";


    private final String UIDAI_CERT_PP =
            "MIIGBjCCBO6gAwIBAgIEAS7tkTANBgkqhkiG9w0BAQsFADCBkDELMAkGA1UEBhMC\n" +
                    "SU4xKjAoBgNVBAoTIWVNdWRocmEgQ29uc3VtZXIgU2VydmljZXMgTGltaXRlZDEd\n" +
                    "MBsGA1UECxMUQ2VydGlmeWluZyBBdXRob3JpdHkxNjA0BgNVBAMTLWUtTXVkaHJh\n" +
                    "IFN1YiBDQSBmb3IgQ2xhc3MgMyBPcmdhbmlzYXRpb24gMjAxNDAeFw0yMDEwMjMx\n" +
                    "NTEzMTVaFw0yMjEwMjMxNTEzMTVaMIIBCTELMAkGA1UEBhMCSU4xDjAMBgNVBAoT\n" +
                    "BVVJREFJMRowGAYDVQQLExFURUNITk9MT0dZIENFTlRSRTFJMEcGA1UEFBNAM2M1\n" +
                    "YTExYzMwMGRhYWJjZGNiODRhOWQwZThlNDQzY2I3ZDVhOTczODRiZTFhODljZGFm\n" +
                    "MWVkOTBmMzdhYmZkOTEPMA0GA1UEERMGNTYwMDkyMRIwEAYDVQQIEwlLQVJOQVRB\n" +
                    "S0ExSTBHBgNVBAUTQDg3Yjc1NmY0OWVmZWNiYTE3Mzc5ZTk2OTdiYTJjZjMxZGE3\n" +
                    "ZThmODRhNmU4ZjRkYThkMDViMTgzZmI1ZDFhZDkxEzARBgNVBAMTCkFOVVAgS1VN\n" +
                    "QVIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDHRH2w+a6uAMp3jX4/\n" +
                    "u6nsLLjHhrLjHXU4HazcrovBYLKymjXhmEAFudhKZcEkEUiUu/z1EggQ8vrmukdZ\n" +
                    "mP7qFQG9RQMtslXs/1K1L2hxq6CFJLlKqR4ZWFElahZLFeKwtFnaekwgNek5A8/a\n" +
                    "ffYeRAxzBEJa6NSX9Plecxqo5OBlH8A0cG/1Psb0LWKmwNiHwFWwLYUdNl8Vt860\n" +
                    "kgO6oVAkw6U2oJCbQElOHE1u7aWdVwIEEDPTum8ipme+zYSdlicixFUbLltZBGtv\n" +
                    "7rzj5XZjUGB9XSp2GIRPHLmsZmD+SPChQvGs25+alCjjlQsKJoDdBaFH4PN1fk3v\n" +
                    "fLQ7AgMBAAGjggHqMIIB5jAiBgNVHREEGzAZgRdhbnVwLmt1bWFyQHVpZGFpLm5l\n" +
                    "dC5pbjATBgNVHSMEDDAKgAhM0b0qEUgE0zAdBgNVHQ4EFgQU93tvksvd4jux0Oxx\n" +
                    "14xqSlpLo4cwDAYDVR0TAQH/BAIwADAOBgNVHQ8BAf8EBAMCBSAwGQYDVR0lAQH/\n" +
                    "BA8wDQYLKwYBBAGCNwoDBAEwgYwGA1UdIASBhDCBgTAtBgZggmRkAgMwIzAhBggr\n" +
                    "BgEFBQcCAjAVGhNDbGFzcyAzIENlcnRpZmljYXRlMFAGB2CCZGQBCAIwRTBDBggr\n" +
                    "BgEFBQcCARY3aHR0cDovL3d3dy5lLW11ZGhyYS5jb20vcmVwb3NpdG9yeS9jcHMv\n" +
                    "ZS1NdWRocmFfQ1BTLnBkZjB7BggrBgEFBQcBAQRvMG0wJAYIKwYBBQUHMAGGGGh0\n" +
                    "dHA6Ly9vY3NwLmUtbXVkaHJhLmNvbTBFBggrBgEFBQcwAoY5aHR0cDovL3d3dy5l\n" +
                    "LW11ZGhyYS5jb20vcmVwb3NpdG9yeS9jYWNlcnRzL0MzT1NDQTIwMTQuY3J0MEcG\n" +
                    "A1UdHwRAMD4wPKA6oDiGNmh0dHA6Ly93d3cuZS1tdWRocmEuY29tL3JlcG9zaXRv\n" +
                    "cnkvY3Jscy9DM09TQ0EyMDE0LmNybDANBgkqhkiG9w0BAQsFAAOCAQEALAaczq4W\n" +
                    "5I0FUzC2uDZekYynRM/rMAy9HiTZYRyFBu4DbmfyC8vP1zPnu8LrHkIfBc3FjYQc\n" +
                    "nKMPnlofsPGUaoLc/L48NnsR9d6r/zi/xlkH+4t6tFoRqoPQW0Jz4EWuRh0JsQQ8\n" +
                    "5Fe+2Tx4JZJ9a9j/2JR+dNmPr4mAjs2D+4LmyTp3UagcPnFPlw3a8rnyVel1QZjg\n" +
                    "Am5rEIYv5ZOZpbbj+Lh7Rn9TfPGVxOsGF06XKxEO1/ftbfmsSicEqk7dlInHnPxJ\n" +
                    "hD+iFc9UjRkGzjKLG0VIH/5X53oZbhMR99e6xg2r4I730SNJDyTEDFfCt9Xn7RR2\n" +
                    "Yf8QO5RAyfihJQ==";

    //OLD certificate
//    private final String UIDAI_CERT_S =
//            "MIIDBjCCAe6gAwIBAgIEATMzfzANBgkqhkiG9w0BAQUFADA7MQswCQYDVQQGEwJJ\n" +
//                    "TjEOMAwGA1UEChMFVUlEQUkxHDAaBgNVBAMTE0F1dGhTdGFnaW5nMTYwOTIwMjAw\n" +
//                    "HhcNMTUwOTE2MDAwMDAwWhcNMjAwOTE2MDAwMDAwWjA7MQswCQYDVQQGEwJJTjEO\n" +
//                    "MAwGA1UEChMFVUlEQUkxHDAaBgNVBAMTE0F1dGhTdGFnaW5nMTYwOTIwMjAwggEi\n" +
//                    "MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDGBWEdlnBNuIExu7rM/Ok1F9Wn\n" +
//                    "uV25tm+o+4pZPvtSFHylRSIFbt/X0/47HoSvoroX+GgxbPPaTyB5USoWhFtVRVN/\n" +
//                    "HGjEGSKxDzZYKlsbQqQ80bJn2L/noCyWr9vB9JvIfqt+kCouMW70FfDhb5JjXNMo\n" +
//                    "iOTEKNOHgVuqDOkWQZWVXcCX3w4OuVLu67Jf6p6qO8NncdD4zN6Ots2fpNBEEtpq\n" +
//                    "oJWWRLOvN6NfISpqqWLBU5Wo0jdg917syOXinrIYn1PlnhIZdJBdc/njaRvFuVxa\n" +
//                    "f6KdC8SYsKzedrzjUp+UVGcVwzDbs0MfUpAvwhlkHK7nooToE4iJ1yqgUXZpAgMB\n" +
//                    "AAGjEjAQMA4GA1UdDwEB/wQEAwIFoDANBgkqhkiG9w0BAQUFAAOCAQEAT3l/ShgP\n" +
//                    "46+Ctqrp/WIzheslpxvsSWpD2jwWvinXujXY6Vsc77gPQUsQawKNY0p4h9j8MDSN\n" +
//                    "b8oYY8i7NxxH6kPuIjzoRNJtA1jiKANdFNuEPK9h4wETBlEfgU0yOdWer7inQO3S\n" +
//                    "6pH8eGChhOHxmIqBGIfnjoWq8RbIdRrj4E/xkvvZpVj2Vp1MPyQoVJSQ+tZIAwLH\n" +
//                    "tzcs7UUJUoGyII8egKDX1NFdvRM62wzfCyx5J1wSSaCZ2V/lr7CmTmHcbC04K3BN\n" +
//                    "N5Yby7FxmU5NNrTvW1ZPLVXpvo9hBfnRc+L75PPpoBV9V54wSzsn0rDKjYcpniYT\n" +
//                    "cpm09Ae8SAS0vg==";

    private final String UIDAI_CERT_S =
            "MIID5DCCAsygAwIBAgIEATMzfzANBgkqhkiG9w0BAQsFADCBqTELMAkGA1UEBhMC\n" +
                    "SU4xEjAQBgNVBAgTCUthcm5hdGFrYTESMBAGA1UEBxMJQmFuZ2Fsb3JlMQ4wDAYD\n" +
                    "VQQKEwVVSURBSTEcMBoGA1UECxMTQXV0aFN0YWdpbmcyNTA4MjAyNTEcMBoGA1UE\n" +
                    "AxMTQXV0aFN0YWdpbmcyNTA4MjAyNTEmMCQGCSqGSIb3DQEJARYXYW51cC5rdW1h\n" +
                    "ckB1aWRhaS5uZXQuaW4wHhcNMjAwODI1MDAwMDAwWhcNMjUwODI1MDAwMDAwWjCB\n" +
                    "qTELMAkGA1UEBhMCSU4xEjAQBgNVBAgTCUthcm5hdGFrYTESMBAGA1UEBxMJQmFu\n" +
                    "Z2Fsb3JlMQ4wDAYDVQQKEwVVSURBSTEcMBoGA1UECxMTQXV0aFN0YWdpbmcyNTA4\n" +
                    "MjAyNTEcMBoGA1UEAxMTQXV0aFN0YWdpbmcyNTA4MjAyNTEmMCQGCSqGSIb3DQEJ\n" +
                    "ARYXYW51cC5rdW1hckB1aWRhaS5uZXQuaW4wggEiMA0GCSqGSIb3DQEBAQUAA4IB\n" +
                    "DwAwggEKAoIBAQCtnXWu8+uja+Us3z+TWjY1yV5KZq8I4CT9oHVk0hOMOhZz5Vas\n" +
                    "h4mvj4mHa8u9y2/qZXIdIB8s006k2jz0dvnpBiMFzoJoQ5TSPwJl13gGKu/NTPro\n" +
                    "BIELiDnOESfOFevQas48hMbHxvRIIrTUIZ+wL017uXCF/UIamdwRZ8SSoN897tWw\n" +
                    "rRmSutpsgDCE/F4k88XzfOyx2UyG+kJJZOYIWeYWMhLRH4ascP/OE1/9BtJ31wZE\n" +
                    "ZFEUp0Saat5KNWLlDhKF4R8mwJc7+OMIOw5YPyjY/iW/OyoEwgxvjgqCizlWZnv+\n" +
                    "oRq8yBxtBkfwkakwxYv1rOamNbHpET30EB2TAgMBAAGjEjAQMA4GA1UdDwEB/wQE\n" +
                    "AwIF4DANBgkqhkiG9w0BAQsFAAOCAQEAVGhmm2h3d8aOBhoZonAN6C5W1NY0hsuK\n" +
                    "P7xZ3ZyVeEhs1/DIavaPmrNx3LISEJZ9UDwGJdP/6+1M86DXUK5dvyjpfQOESxnX\n" +
                    "FNqvbuQkh2C/IxawCWjQCjWgUm+yyRXnpvcgLGNYGhKxnmuZVJwJOlScc/6wjqvO\n" +
                    "NscPV+neHwerrbFBq8DwXGgqiJU2dijRFpChhN09PSbkQ/y2ACOBOS87XJrcxBP+\n" +
                    "AyBSTdQNG+q94Ww/PKBDgIvnR2JzpYA+eHqu45CJDy5zA1oHT1N7JZlm5GPe798g\n" +
                    "5GMrBfd/CZ5GTeGRS+MNSAGmD3BjankxWFWMVdNiXjLs400EZdKQGg==";

    private int errCode = 0;
    private String status = "NOTREADY";

    private String imei;
    private String serial;
    private String company;
    private String address;
    private String pincode;
    private String mobile;
    private String uidaiCert;

    private String env,mc,dc;
    private String privateKeyString;

    private PidOptions pidOptions;
    private PidData pidData;

    public String getMc() {
        return mc;
    }

    public String getDc() {
        return dc;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial){
        this.serial = serial;
    }

    public String getUidaiCert(String env) {
        return env.equals("P")?
                UIDAI_CERT_P:
                env.equals("PP")?
                        UIDAI_CERT_PP:
                        env.equals("S")?
                                UIDAI_CERT_S:
                                "";
    }

    public String getUidaiCert() {
        if(uidaiCert!=null)return uidaiCert;
        if(env==null)return UIDAI_CERT_P;
        switch(env){
            case "S": return UIDAI_CERT_S;
            case "PP": return UIDAI_CERT_PP;
            default: return UIDAI_CERT_P;
        }
    }

    public String getPrivateKeyString() {
        return privateKeyString;
    }

    public void setPrivateKeyString(String privateKeyString) {
        this.privateKeyString = privateKeyString;
    }

    public String getCompany() {
        return company;
    }

    public String getAddress() {
        return address;
    }

    public String getPincode() {
        return pincode;
    }

    public String getMobile() {
        return mobile;
    }

    public String getImei() {
        return imei;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errorCode) {
        this.errCode = errorCode;
    }

//    public void update(BioEnablePacketResponse response,String env){
//        this.env = env;
//        this.mc = response.getSigned_device_public_key();
//        this.dc = response.getUuid_value();
//
////        this.certPath = response.getNewCerPath()==null?null:response.getNewCerPath()
////                .replace("-----BEGIN CERTIFICATE-----","")
////                .replace("-----END CERTIFICATE-----","").trim();
//
//    }

    public void update(String env,String dc,String mc,String uidaiCert){
//        this.env = store.read(PersistentStore.Key.ENV);
//        this.mc = store.read(PersistentStore.Key.MC);
//        this.dc = store.read(PersistentStore.Key.DC);
        this.env = env;
        this.dc = dc;
        this.mc = mc;
        this.uidaiCert = uidaiCert;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PidOptions getPidOptions() {
        return pidOptions;
    }

    public void setPidOptions(PidOptions pidOptions) {
        this.pidOptions = pidOptions;
    }

    public PidData getPidData(){
        if(pidData==null)pidData = new PidData();
        return pidData;
    }
}