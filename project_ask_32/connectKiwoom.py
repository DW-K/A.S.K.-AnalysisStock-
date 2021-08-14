from pykiwoom.kiwoom import Kiwoom


def connectKiwoom():
    kiwoom = Kiwoom()
    kiwoom.CommConnect(block=True)
    return kiwoom


if __name__ == "__main__":
    connectKiwoom()