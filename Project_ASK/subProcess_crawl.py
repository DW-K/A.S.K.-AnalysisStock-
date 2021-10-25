import subprocess
import json


def subProcess_crawl(func, arg=[]):
    path = r'..\resource\environ_crawling.json'

    with open(path, 'r') as f:
        json_data = json.load(f)
        env = json_data['env']
        interpreter = json_data['interpreter']
    kwargs = {"stdin": subprocess.PIPE, "stdout": subprocess.PIPE, "env": env}
    with subprocess.Popen([interpreter, fr'..\ask_crawling\{func}.py'] + arg, **kwargs,
                          shell=True) as proc:
        out, err = proc.communicate()
        if err is not None:
            print(err)

        # print(out)

    print(f"complete crawling news: {arg[1]}")


def crawling_news(num, query, sort, s_date, e_date, isSentiment=True):
    if isSentiment:
        subProcess_crawl('sentiment_news', [num, query, sort, s_date, e_date])
    else:
        subProcess_crawl('crawling_news', [num, query, sort, s_date, e_date])


def crawling_tweet():
    subProcess_crawl('crawling_tweet')


if __name__ == "__main__":
    subProcess_crawl('test', ["joke", "1234"])
