#!/bin/bash

# 설정 값
bucket_name="gidosa-deploys"
folder_path="dev-gidosa-full-webadmin/"
file_count_to_keep=10

# 특정 폴더 내 객체들을 가져오고 최신 타임스탬프로 정렬
objects=$(aws s3api list-objects-v2 --bucket "$bucket_name" --prefix "$folder_path" --query 'sort_by(Contents,&LastModified)' --output json)
#echo "$objects"

# JSON 데이터의 항목 수 계산
item_count=$(echo "$objects" | jq length)
#echo "$item_count"

# file_count_to_keep 값 이하인 경우 그대로 두고 file_count_to_keep값 이상이면 file_count_to_keep값의 파일 개수는 남겨두고 가장 오래된 파일부터 삭제
item_count=$((item_count - file_count_to_keep))
if ((item_count <= 0)); then
        #objects=''
        echo 'no exist delete deploy file'
        exit 0
else
        objects=$(echo "$objects" | jq "sort_by(.LastModified) | .[:$item_count]")
fi
#echo "$objects"

# Key가 '/'로 끝나는 객체 필터링
filtered_data=$(echo "$objects" | jq 'map(select(.Key | endswith("/") | not))')
#echo "$filtered_data"

key_data=$(echo "$filtered_data" | jq -r '.[] | "\(.Key)"')
#echo "$key_data"

if [ -n "$key_data" ]; then
        echo "$key_data" | while read -r key; do
                aws s3api delete-object --bucket "$bucket_name" --key "$key"
        done
else
        echo 'no exist delete deploy file2'
        exit 0
fi
echo 'completed'
