import torch
import torch.nn as nn
import torch.nn.functional as F

class Net(nn.Module): # nn.Module 모든 신경망 모듈의 기본이 되는 클래스
                      # 각 층과 함수 등 신경망의 구성요소를 이 클래스 안에서 정의한다.
                      # nn.Module은 모든 신경망 모듈의 기본이 되는 클래스로 레이어, 함수등을 정의하는구나!

    def __init__(self):  # 초기화 함수
        super(Net, self).__init__()
        # 1 input image channel, 6 output channels, 3x3 square convolution kernel
        # (kernel과 filter는 같다) 즉, filter size는 3x3
        self.conv1 = nn.Conv2d(1, 6, 3)  # 입력 채널 수, 출력 채널 수, 필터의 크기
        self.conv2 = nn.Conv2d(6, 16, 3) # 마찬가지 입력 채널 수, 출력 채널 수, 필터의 크기

        # an affine operation: y = Wx + b
        # input_image의 dimension을 6x6이라고 가정하자
        self.fc1 = nn.Linear(16 * 6 * 6, 120)  # Q1. 매개변수 값은 어떻게 계산해야할까?
                                               # 6*6 from image dimension? -20.02.20.Thur pm 2:00-
                                               # image dimension은 어떻게 구하지? -20.02.20.Thur pm 8:05-
                                               # https://discuss.pytorch.org/t/linear-layer-input-neurons-number-calculation-after-conv2d/28659
                                               # Set the number of in_features for the first linear layer to (outputchanel * size of image)
                                               # output_chanel_num * height * width
        self.fc2 = nn.Linear(120, 84)
        self.fc3 = nn.Linear(84, 10)

    def forward(self, x):
        # Max pooling over a (2, 2) window
        x = F.max_pool2d(F.relu(self.conv1(x)), (2, 2))
        # If the size is a square you can only specify a single number
        x = F.max_pool2d(F.relu(self.conv2(x)), 2)
        x = x.view(-1, self.num_flat_features(x))
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
        x = self.fc3(x)
        return x

    def num_flat_features(self, x):
        size = x.size()[1:]  # all dimensions except the batch dimension
        num_features = 1
        for s in size:
            num_features *= s
        return num_features

# Affine Operation에서 Linear 계층의 매개변수는 어떻게 정해지는건가? -20.02.20.Thur pm 7:49
# 여전히 미스테리다(20.02.21.Fri.pm 12:22)... Torch Doc을 뒤져보자
# nn.Linear()함수의 첫 벗째 인자에는 input sample의 size가 들어간다.
# 두 번째 인자에는 output sample의 size가 들어간다.
# clas:: torch.nn.Linear(in_features, out_features, bias=True)
# 본 예제에서는 첫 인자로 16 * 6 * 6의 값을 인자로 받는데
# 이는 Conv2 계층을 지난 출력 채널의 수가 16, input_image의 dimension이 6*6이기 때문이다.
# 정리하면 self1.fc1 = nn.Linear(16 * 6 * 6, 120)이 된다.

net = Net()
print(net)
# Net(
#   (conv1): Conv2d(1, 6, kernel_size=(3, 3), stride=(1, 1))
#   (conv2): Conv2d(6, 16, kernel_size=(3, 3), stride=(1, 1))
#   (fc1): Linear(in_features=576, out_features=120, bias=True)
#   (fc2): Linear(in_features=120, out_features=84, bias=True)
#   (fc3): Linear(in_features=84, out_features=10, bias=True)
# )