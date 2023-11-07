FROM public.ecr.aws/aleph0io/lambda/java:21
COPY target/*-aws.jar "${LAMBDA_TASK_ROOT}/lib/"
CMD [ "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest" ]
