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

    # print(f"complete crawling news: {arg[1]} ({arg[3]}): {arg[5]}-{arg[6]}")
    print(out.decode())


def crawling_news(companyName, query, s_date, e_date=None):
    if e_date is None:
        e_date = s_date

    subProcess_crawl('crawling_news', [category, companyName, maxpage, query, s_date, e_date])
    print(f"complete crawling news: {companyName} ({query}): {s_date}-{e_date}")


def crawling_tweet(companyName, query, s_date, e_date=None):
    if e_date is None:
        e_date = s_date

    subProcess_crawl('crawling_tweet', [category, companyName, maxpage, query, s_date, e_date])
    print(f"complete crawling tweet: {companyName} ({query}): {s_date}-{e_date}")


def integrate_word_count(category, companyName, target_date):
    subProcess_crawl('integrate_word_count', [category, companyName, target_date])
    print(f"complete integrate word count ({companyName}_{target_date})")


if __name__ == "__main__":
    subProcess_crawl('test', ["joke", "1234"])
